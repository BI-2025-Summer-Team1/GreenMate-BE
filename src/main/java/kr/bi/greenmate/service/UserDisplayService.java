package kr.bi.greenmate.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import kr.bi.greenmate.entity.User;

@Component
public class UserDisplayService {

	@Value("${app.default-profile-image-url}")
	private String defaultProfileImageUrl;

	public String displayName(User user) {
		if (user == null || user.getDeletedAt() != null)
			return "탈퇴한 회원";
		return user.getNickname();
	}

	public String profileImage(User user) {
		if (user == null || user.getDeletedAt() != null)
			return defaultProfileImageUrl;
		return user.getProfileImageUrl();
	}
}
