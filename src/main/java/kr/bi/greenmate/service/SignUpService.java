package kr.bi.greenmate.service;

import jakarta.transaction.Transactional;
import kr.bi.greenmate.dto.SignUpRequest;
import kr.bi.greenmate.dto.SignUpResponse;
import kr.bi.greenmate.entity.User;
import kr.bi.greenmate.exception.EmailDuplicateException;
import kr.bi.greenmate.exception.NicknameDuplicateException;
import kr.bi.greenmate.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class SignUpService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final ImageUploadService imageUploadService;

    @Transactional
    public SignUpResponse signUp(SignUpRequest request) {
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new EmailDuplicateException();
        }
        if (userRepository.findByNickname(request.getNickname()).isPresent()) {
            throw new NicknameDuplicateException();
        }

        String encodedPassword = passwordEncoder.encode(request.getPassword());

        String profileImageUrl;

        MultipartFile profileImage = request.getProfileImage();
        if (profileImage != null && !profileImage.isEmpty()) {
            profileImageUrl = imageUploadService.upload(profileImage, "profile");
        }
        else {
            profileImageUrl = "default.png"; // 기본 이미지
        }

        User user = User.builder()
                .email(request.getEmail())
                .nickname(request.getNickname())
                .password(encodedPassword)
                .profileImageUrl(profileImageUrl)
                .selfIntroduction(request.getSelfIntroduction())
                .build();

        User savedUser = userRepository.save(user);

        return SignUpResponse.builder()
                .userId(savedUser.getId())
                .message("회원가입이 완료되었습니다.")
                .build();
    }
}
