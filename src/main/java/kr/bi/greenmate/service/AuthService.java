package kr.bi.greenmate.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.hibernate.exception.ConstraintViolationException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import kr.bi.greenmate.common.enums.ImageType;
import kr.bi.greenmate.config.JwtProvider;
import kr.bi.greenmate.dto.LoginRequest;
import kr.bi.greenmate.dto.LoginResponse;
import kr.bi.greenmate.dto.SignUpRequest;
import kr.bi.greenmate.dto.SignUpResponse;
import kr.bi.greenmate.entity.Agreement;
import kr.bi.greenmate.entity.User;
import kr.bi.greenmate.entity.UserAgreement;
import kr.bi.greenmate.entity.UserAgreementId;
import kr.bi.greenmate.exception.error.AgreementNotFoundException;
import kr.bi.greenmate.exception.error.EmailDuplicateException;
import kr.bi.greenmate.exception.error.FileEmptyException;
import kr.bi.greenmate.exception.error.FileUploadFailException;
import kr.bi.greenmate.exception.error.InvalidImageTypeException;
import kr.bi.greenmate.exception.error.MissingImageTypeException;
import kr.bi.greenmate.exception.error.RequiredAgreementsNotAcceptedException;
import kr.bi.greenmate.exception.error.SignUpFailException;
import kr.bi.greenmate.exception.error.UserNotFoundException;
import kr.bi.greenmate.repository.AgreementRepository;
import kr.bi.greenmate.repository.UserAgreementRepository;
import kr.bi.greenmate.repository.UserRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthService {
	private final JwtProvider jwtProvider;
	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;
	private final ImageUploadService imageUploadService;
	private final AgreementRepository agreementRepository;
	private final UserAgreementRepository userAgreementRepository;

	@Transactional
	public SignUpResponse signUp(SignUpRequest request, MultipartFile profileImage) {

		String profileImageUrl = uploadProfileImage(profileImage);

		User user = createUser(request, profileImageUrl);

		User savedUser = saveUser(user);

		saveUserAgreements(savedUser, request.getAcceptedAgreementIds());

		return SignUpResponse.builder()
			.userId(savedUser.getId())
			.message("회원가입이 완료되었습니다.")
			.build();
	}

	@Transactional(readOnly = true)
	public LoginResponse login(LoginRequest request) {
		User user = userRepository.findByEmail(request.getEmail()).orElseThrow(UserNotFoundException::new);

		if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
			throw new UserNotFoundException();
		}

		String accessToken = jwtProvider.createToken(user.getId(), user.getEmail(), user.getNickname());

		return new LoginResponse(accessToken);
	}

	private String uploadProfileImage(MultipartFile profileImage) {
		if (profileImage != null && !profileImage.isEmpty()) {
			try {
				return imageUploadService.upload(profileImage, ImageType.COMMUNITY.getValue());
			} catch (FileEmptyException | MissingImageTypeException | InvalidImageTypeException e) {
				throw e;
			} catch (Exception e) {
				throw new FileUploadFailException();
			}
		}
		return null;
	}

	private User createUser(SignUpRequest request, String profileImageUrl) {
		return User.builder()
			.email(request.getEmail())
			.nickname(request.getNickname())
			.password(passwordEncoder.encode(request.getPassword()))
			.profileImageUrl(profileImageUrl)
			.build();
	}

	private User saveUser(User user) {
		try {
			return userRepository.save(user);
		} catch (DataIntegrityViolationException e) {
			Throwable cause = e.getMostSpecificCause();
			if (cause instanceof ConstraintViolationException violation) {
				String constraintName = violation.getConstraintName();
				if ("uk_user_email".equals(constraintName)) {
					throw new EmailDuplicateException();
				}
			}
			throw new SignUpFailException();
		}
	}

	public boolean isNicknameDuplicate(String nickname) {
		return userRepository.existsByNickname(nickname);
	}

	private void saveUserAgreements(User user, Set<Long> acceptedAgreementIds) {

		Set<Long> accepted;
		if (acceptedAgreementIds == null) {
			accepted = Collections.emptySet();
		} else {
			accepted = acceptedAgreementIds;
		}

		List<Agreement> required = agreementRepository.findByIsRequiredTrue();
		Set<Long> requiredIds = required.stream()
			.map(Agreement::getId).collect(Collectors.toSet());
		if (!accepted.containsAll(requiredIds)) {
			throw new RequiredAgreementsNotAcceptedException();
		}

		List<Agreement> acceptedAgreements = agreementRepository.findAllById(accepted);
		if (accepted.size() != acceptedAgreements.size()) {
			throw new AgreementNotFoundException();
		}

		Map<Long, UserAgreement> previousAgreements =
			userAgreementRepository.findByUserAgreementIdUserId(user.getId()).stream()
				.collect(Collectors.toMap(ua -> ua.getUserAgreementId().getAgreementId(), ua -> ua));

		LocalDateTime now = LocalDateTime.now();
		List<UserAgreement> toSave = new ArrayList<>();

		for (Agreement agreement : acceptedAgreements) {
			UserAgreement userAgreement = previousAgreements.get(agreement.getId());
			if (userAgreement != null) {
				userAgreement.accept(now);
			} else {
				UserAgreement newUserAgreement = UserAgreement.builder()
					.userAgreementId(new UserAgreementId(user.getId(), agreement.getId()))
					.isAccepted(true)
					.acceptedAt(now)
					.user(user)
					.agreement(agreement)
					.build();
				toSave.add(newUserAgreement);
			}
		}

		for (UserAgreement userAgreement : previousAgreements.values()) {
			Long agreementId = userAgreement.getUserAgreementId().getAgreementId();
			if (!accepted.contains(agreementId)) {
				userAgreement.revoke(now);
			}
		}

		userAgreementRepository.saveAll(toSave);
	}
}
