package kr.bi.greenmate.service;

import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import kr.bi.greenmate.repository.ObjectStorageRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class StorageDeleteListener {

	private final ObjectStorageRepository storage;

	@Async
	@TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
	@Retryable(maxAttempts = 3, backoff = @Backoff(delay = 200))
	public void on(ImagesToDeleteEvent e) {
		for (String key : e.keys()) {
			try {
				if (key != null)
					storage.delete(key);
			} catch (Exception ex) {
				log.warn("object storage delete failed key={}", key, ex);
				throw ex;
			}
		}
	}
}
