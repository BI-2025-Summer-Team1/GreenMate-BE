package kr.bi.greenmate.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.redisson.api.RLock;
import org.redisson.api.RMap;
import org.redisson.api.RScript;
import org.redisson.api.RedissonClient;
import org.redisson.client.codec.LongCodec;
import org.redisson.client.codec.StringCodec;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import kr.bi.greenmate.exception.error.RedisConnectionFailException;
import kr.bi.greenmate.exception.error.ViewCountFlushFailException;
import kr.bi.greenmate.exception.error.ViewCountPersistFailException;
import kr.bi.greenmate.repository.CommunityPostRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class ViewCountService {

	private static final String VIEW_KEY_PREFIX = "community:view:";
	private static final String DIRTY_SET_KEY = "community:view:dirty";
	private static final String FLUSH_LOCK_KEY = "community:view:flush:lock";
	private static final String PENDING_HASH_KEY = "community:view:pending";
	private static final String LUA_INCREMENT_AND_MARK_DIRTY = String.join("\n",
		// KEYS[1] = counterKey, KEYS[2] = dirtySetKey
		// ARGV[1] = postId(string)
		"local v = redis.call('INCR', KEYS[1])",
		"if (v == 1) then",
		"  redis.call('SADD', KEYS[2], ARGV[1])",
		"end",
		"return v"
	);
	private static final String LUA_MOVE_COUNTER_TO_PENDING = String.join("\n",
		// KEYS[1] = counterKey, KEYS[2] = pendingHashKey, KEYS[3] = dirtySetKey
		// ARGV[1] = postId(string)
		"local v = tonumber(redis.call('GET', KEYS[1]) or '0')",
		"if (v > 0) then",
		"  redis.call('SET', KEYS[1], 0)",
		"  redis.call('HINCRBY', KEYS[2], ARGV[1], v)",
		"end",
		"local cur = tonumber(redis.call('GET', KEYS[1]) or '0')",
		"if (cur == 0) then",
		"  redis.call('SREM', KEYS[3], ARGV[1])",
		"end",
		"return v"
	);
	private final RedissonClient redissonClient;
	private final CommunityPostRepository communityPostRepository;

	public long increment(long postId) {
		try {
			String counterKey = VIEW_KEY_PREFIX + postId;
			Long newValue = redissonClient.getScript(StringCodec.INSTANCE).eval(
				RScript.Mode.READ_WRITE,
				LUA_INCREMENT_AND_MARK_DIRTY,
				RScript.ReturnType.INTEGER,
				Arrays.asList(counterKey, DIRTY_SET_KEY),
				String.valueOf(postId)
			);
			return newValue != null ? newValue : 0L;
		} catch (Exception e) {
			log.error("조회수 증가 처리 실패 - postId: {}, 원인: {}", postId, e.getMessage());
			throw new RedisConnectionFailException();
		}
	}

	public long getDelta(long postId) {
		try {
			return redissonClient.getAtomicLong(VIEW_KEY_PREFIX + postId).get();
		} catch (Exception e) {
			log.error("조회수 조회 실패 - postId: {}, 원인: {}", postId, e.getMessage());
			throw new RedisConnectionFailException();
		}
	}

	@Scheduled(fixedDelayString = "${view.flush-interval-ms:5000}")
	public void flush() {
		RLock lock = redissonClient.getLock(FLUSH_LOCK_KEY);
		boolean locked = false;
		try {
			locked = lock.tryLock();
			if (!locked)
				return;

			int moved = moveDirtyCountersToPending();
			int persisted = persistPendingToDatabase();

			log.debug("View flush: moved={}, persisted={}", moved, persisted);
		} catch (Exception e) {
			log.error("조회수 동기화 처리 중 예상치 못한 오류 발생", e);
			throw new ViewCountFlushFailException();
		} finally {
			if (locked) {
				try {
					lock.unlock();
				} catch (Exception ignore) {
				}
			}
		}
	}

	private int moveDirtyCountersToPending() {
		try {
			Set<Object> idObjects = redissonClient.getSet(DIRTY_SET_KEY, StringCodec.INSTANCE).readAll();
			if (idObjects.isEmpty())
				return 0;

			List<String> keys = new ArrayList<>(3);
			int tried = 0;

			for (Object idObj : idObjects) {
				String idStr = idObj.toString();
				tried++;
				String counterKey = VIEW_KEY_PREFIX + idStr;

				Long moved = redissonClient.getScript(StringCodec.INSTANCE).eval(
					RScript.Mode.READ_WRITE,
					LUA_MOVE_COUNTER_TO_PENDING,
					RScript.ReturnType.INTEGER,
					Arrays.asList(counterKey, PENDING_HASH_KEY, DIRTY_SET_KEY),
					idStr
				);
			}
			return tried;
		} catch (Exception e) {
			log.error("조회수 pending 이동 처리 실패 - 원인: {}", e.getMessage());
			throw new RedisConnectionFailException();
		}
	}

	private int persistPendingToDatabase() {
		try {
			RMap<String, Long> pending = redissonClient.getMap(PENDING_HASH_KEY, LongCodec.INSTANCE);

			Set<Map.Entry<String, Long>> entries = pending.readAllEntrySet();
			if (entries.isEmpty())
				return 0;

			int success = 0;
			for (Map.Entry<String, Long> e : entries) {
				String idStr = e.getKey();
				Long delta = e.getValue();
				if (delta == null || delta <= 0L) {
					pending.fastRemove(idStr);
					continue;
				}

				long id = Long.parseLong(idStr);
				try {
					persistOne(id, delta);
					pending.fastRemove(idStr);
					success++;
				} catch (Exception ex) {
					log.warn("조회수 DB 반영 실패 - id: {}, delta: {}, 원인: {}", id, delta, ex.getMessage());
					throw new ViewCountPersistFailException();
				}
			}
			return success;
		} catch (ViewCountPersistFailException e) {
			throw e;
		} catch (Exception e) {
			log.error("조회수 pending 처리 중 예상치 못한 오류 발생 - 원인: {}", e.getMessage());
			throw new RedisConnectionFailException();
		}
	}

	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public void persistOne(long postId, long delta) {
		try {
			communityPostRepository.incrementViewCountBy(postId, delta);
		} catch (Exception e) {
			log.error("개별 조회수 DB 반영 실패 - postId: {}, delta: {}, 원인: {}", postId, delta, e.getMessage());
			throw new ViewCountPersistFailException();
		}
	}
}
