package kr.bi.greenmate.service;

import java.time.Duration;
import java.util.List;

import org.redisson.api.RList;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;

import kr.bi.greenmate.entity.ChatMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChatRedisService {

	private final RedissonClient redissonClient;

	private static final String SESSION_KEY_PREFIX = "chat:session:";
	private static final String HISTORY_KEY_PREFIX = "chat:history:";
	private static final Duration SESSION_TTL = Duration.ofHours(24);
	private static final Duration HISTORY_TTL = Duration.ofHours(24);

	public void setCurrentSessionId(Long userId, Long sessionId) {
		String key = SESSION_KEY_PREFIX + userId;
		redissonClient.getBucket(key).set(sessionId, SESSION_TTL);
	}

	public Long getCurrentSessionId(Long userId) {
		String key = SESSION_KEY_PREFIX + userId;
		return (Long)redissonClient.getBucket(key).get();
	}

	public void addMessageToHistory(Long userId, Long sessionId, ChatMessage message) {
		String key = HISTORY_KEY_PREFIX + userId + ":" + sessionId;
		RList<ChatMessage> list = redissonClient.getList(key);
		list.add(0, message);
		list.expire(HISTORY_TTL);

		if (list.size() > 50) {
			list.trim(0, 49);
		}
	}

	public List<ChatMessage> getChatHistory(Long userId, Long sessionId) {
		String key = HISTORY_KEY_PREFIX + userId + ":" + sessionId;
		RList<ChatMessage> list = redissonClient.getList(key);
		return list.readAll();
	}
}
