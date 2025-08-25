package kr.bi.greenmate.service;

import java.util.function.Supplier;

import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Component;

import jakarta.persistence.OptimisticLockException;
import kr.bi.greenmate.exception.error.OptimisticLockCustomException;

@Component
public class OptimisticLockExecutor {
	private final int maxAttempts = 3;
	private final long backoffMillis = 50;

	public <T> T executeWithRetry(Supplier<T> action) {
		int attempts = 0;
		while (true) {
			try {
				return action.get();
			} catch (OptimisticLockException | ObjectOptimisticLockingFailureException e) {
				if (++attempts >= maxAttempts) {
					throw new OptimisticLockCustomException();
				}
				try {
					Thread.sleep(backoffMillis);
				} catch (InterruptedException ie) {
					Thread.currentThread().interrupt();
					throw new OptimisticLockCustomException();
				}
			}
		}
	}
}
