package kr.bi.greenmate.service;

import java.util.Map;
import java.util.Set;

import org.redisson.api.RAtomicLong;
import org.redisson.api.RLock;
import org.redisson.api.RMap;
import org.redisson.api.RedissonClient;
import org.redisson.client.codec.LongCodec;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.OptimisticLockException;
import kr.bi.greenmate.entity.RecruitmentPost;
import kr.bi.greenmate.exception.error.RecruitmentPostNotFoundException;
import kr.bi.greenmate.exception.error.RedisConnectionFailException;
import kr.bi.greenmate.exception.error.ViewCountFlushFailException;
import kr.bi.greenmate.exception.error.ViewCountPersistFailException;
import kr.bi.greenmate.repository.RecruitmentPostRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class RecruitmentPostViewCountService {

    private static final String VIEW_KEY_PREFIX = "recruitment:view:";
    private static final String PENDING_HASH_KEY = "recruitment:view:pending";
    private static final String FLUSH_LOCK_KEY = "recruitment:view:flush:lock";

    private final RedissonClient redissonClient;
    private final RecruitmentPostRepository recruitmentPostRepository;

    public long increment(long postId) {
        try {
            RAtomicLong counter = redissonClient.getAtomicLong(VIEW_KEY_PREFIX + postId);
            long newValue = counter.incrementAndGet();

            RMap<String, Long> pending = redissonClient.getMap(PENDING_HASH_KEY, LongCodec.INSTANCE);
            pending.addAndGet(String.valueOf(postId), 0L); // 존재하지 않으면 초기화

            return newValue;
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
            if (!locked) return;

            int moved = moveCountersToPending();
            int persisted = persistPendingToDatabase();

            log.debug("View flush: moved={}, persisted={}", moved, persisted);
        } catch (Exception e) {
            log.error("조회수 동기화 처리 중 예상치 못한 오류 발생", e);
            throw new ViewCountFlushFailException();
        } finally {
            if (locked) {
                try { lock.unlock(); } catch (Exception ignore) {}
            }
        }
    }

    private int moveCountersToPending() {
        try {
            RMap<String, Long> pending = redissonClient.getMap(PENDING_HASH_KEY, LongCodec.INSTANCE);
            Set<String> keys = pending.readAllKeySet();
            int count = 0;

            for (String postIdStr : keys) {
                count++;
                RAtomicLong counter = redissonClient.getAtomicLong(VIEW_KEY_PREFIX + postIdStr);
                long delta = counter.getAndSet(0);

                if (delta > 0) {
                    pending.addAndGet(postIdStr, delta);
                }
            }
            return count;
        } catch (Exception e) {
            log.error("조회수 pending 이동 실패 - 원인: {}", e.getMessage());
            throw new RedisConnectionFailException();
        }
    }

    private int persistPendingToDatabase() {
        try {
            RMap<String, Long> pending = redissonClient.getMap(PENDING_HASH_KEY, LongCodec.INSTANCE);
            Set<Map.Entry<String, Long>> entries = pending.readAllEntrySet();

            if (entries.isEmpty()) return 0;

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
            RecruitmentPost post = recruitmentPostRepository.findById(postId)
                .orElseThrow(() -> new RecruitmentPostNotFoundException(postId));

            post.incrementViewCountBy(delta);

        } catch (OptimisticLockException | ObjectOptimisticLockingFailureException e) {
            log.warn("낙관적 락 충돌 발생: postId={}, delta={}", postId, delta);
            throw new ViewCountPersistFailException();
        } catch (Exception e) {
            log.error("개별 조회수 DB 반영 실패 - postId: {}, delta: {}, 원인: {}", postId, delta, e.getMessage());
            throw new ViewCountPersistFailException();
        }
    }
}
