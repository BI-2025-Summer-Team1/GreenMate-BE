package kr.bi.greenmate.service;

import java.time.Duration;
import java.util.Arrays;
import java.util.List;

import org.redisson.api.RBucket;
import org.redisson.api.RList;
import org.redisson.api.RScript;
import org.redisson.api.RedissonClient;
import org.redisson.client.codec.LongCodec;
import org.redisson.client.codec.StringCodec;
import org.springframework.stereotype.Service;

import kr.bi.greenmate.entity.ChatMessages;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChatRedisService {

	private final RedissonClient redissonClient;

	private static final String SESSION_KEY_PREFIX = "chat:session:";
	private static final String HISTORY_KEY_PREFIX = "chat:history:";
	private static final String SESSION_COUNTER_KEY = "chat:global:sessionId";
	private static final Duration SESSION_TTL = Duration.ofHours(24);
	private static final Duration HISTORY_TTL = Duration.ofHours(24);

	public Long getOrCreateAndRefreshSessionId(Long userId) {
		final String key = SESSION_KEY_PREFIX + userId;
		final RBucket<Long> bucket = redissonClient.getBucket(key, LongCodec.INSTANCE);

		Long existing = getSessionIdSafely(bucket);
		if (existing != null) {
			bucket.expire(SESSION_TTL);
			return existing;
		}

		for (int i = 0; i < 3; i++) {
			final Long newId = redissonClient.getScript(StringCodec.INSTANCE)
				.eval(RScript.Mode.READ_WRITE,
					"return redis.call('INCR', KEYS[1])",
					RScript.ReturnType.INTEGER,
					Arrays.asList(SESSION_COUNTER_KEY)
				);

			final boolean success = bucket.setIfAbsent(newId, SESSION_TTL);
			if (success)
				return newId;

			final Long other = getSessionIdSafely(bucket);
			if (other != null) {
				bucket.expire(SESSION_TTL);
				return other;
			}
		}

		final Long last = getSessionIdSafely(bucket);
		if (last != null) {
			bucket.expire(SESSION_TTL);
			return last;
		}
		final Long newId = redissonClient.getScript(StringCodec.INSTANCE)
			.eval(RScript.Mode.READ_WRITE,
				"return redis.call('INCR', KEYS[1])",
				RScript.ReturnType.INTEGER,
				Arrays.asList(SESSION_COUNTER_KEY)
			);
		bucket.setIfAbsent(newId, SESSION_TTL);
		return newId;
	}

	public Long createNewSession(Long userId) {
		final String sessionKey = SESSION_KEY_PREFIX + userId;
		final RBucket<Long> bucket = redissonClient.getBucket(sessionKey, LongCodec.INSTANCE);

		Long old = getSessionIdSafely(bucket);
		if (old != null) {
			try {
				redissonClient.getKeys().delete(HISTORY_KEY_PREFIX + userId + ":" + old);
			} catch (Exception ignored) {
			}
		}

		final Long newId = redissonClient.getScript(StringCodec.INSTANCE)
			.eval(RScript.Mode.READ_WRITE,
				"return redis.call('INCR', KEYS[1])",
				RScript.ReturnType.INTEGER,
				Arrays.asList(SESSION_COUNTER_KEY)
			);

		bucket.set(newId, SESSION_TTL);
		return newId;
	}

	private Long getSessionIdSafely(RBucket<Long> bucket) {
		try {
			return bucket.get();
		} catch (Exception e) {
			log.warn("세션 ID 읽기 실패, null 반환: {}", e.getMessage());
			return null;
		}
	}

	public Long getSessionId(Long userId) {
		String key = SESSION_KEY_PREFIX + userId;
		RBucket<Long> bucket = redissonClient.getBucket(key, LongCodec.INSTANCE);
		return getSessionIdSafely(bucket);
	}

	public void addMessageToHistory(Long userId, Long sessionId, ChatMessages message) {
		String key = HISTORY_KEY_PREFIX + userId + ":" + sessionId;
		RList<ChatMessages> list = redissonClient.getList(key);
		list.add(0, message);
		list.expire(HISTORY_TTL);

		if (list.size() > 50) {
			list.trim(0, 49);
		}
	}

	public List<ChatMessages> getChatHistory(Long userId, Long sessionId) {
		String key = HISTORY_KEY_PREFIX + userId + ":" + sessionId;
		RList<ChatMessages> list = redissonClient.getList(key);
		return list.readAll();
	}

}
