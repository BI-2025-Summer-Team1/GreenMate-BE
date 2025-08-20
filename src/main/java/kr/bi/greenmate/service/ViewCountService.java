package kr.bi.greenmate.service;

import kr.bi.greenmate.repository.CommunityPostRepository;
import lombok.RequiredArgsConstructor;
import org.redisson.api.RAtomicLong;
import org.redisson.api.RLock;
import org.redisson.api.RSet;
import org.redisson.api.RedissonClient;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class ViewCountService {

    private static final String VIEW_KEY_PREFIX = "community:view:";
    private static final String DIRTY_SET_KEY = "community:view:dirty";
    private static final String FLUSH_LOCK_KEY = "community:view:flush:lock";

    private final RedissonClient redissonClient;
    private final CommunityPostRepository communityPostRepository;

    public void increment(long postId){
        RAtomicLong counter = redissonClient.getAtomicLong(VIEW_KEY_PREFIX + postId);
        counter.incrementAndGet();
        redissonClient.<Long>getSet(DIRTY_SET_KEY).add(postId);
    }

    public long getDelta(long postId) {
        return redissonClient.getAtomicLong(VIEW_KEY_PREFIX + postId).get();
    }

    @Scheduled(fixedDelayString = "${view.flush-interval-ms:5000}")
    @Transactional
    public void flush(){
        RLock lock = redissonClient.getLock(FLUSH_LOCK_KEY);
        boolean locked = false;
        try {
            locked = lock.tryLock(0, 30, TimeUnit.SECONDS);
            if (!locked) return;
            flushOnce();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } finally {
            if (locked) lock.unlock();
        }
    }

    private void flushOnce() {
        RSet<Long> dirty = redissonClient.getSet(DIRTY_SET_KEY);
        for (Long id : dirty) {
            RAtomicLong counter = redissonClient.getAtomicLong(VIEW_KEY_PREFIX + id);
            long delta = counter.getAndSet(0L);

            if (delta > 0L) {
                try {
                    communityPostRepository.incrementViewCountBy(id, delta);
                } catch (Exception e) {
                    counter.addAndGet(delta); // write-back
                    throw e;
                }
            }

            if (counter.get() == 0L) {
                dirty.remove(id);
            }
        }
    }
}
