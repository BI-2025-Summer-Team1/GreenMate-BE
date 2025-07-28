package kr.bi.greenmate.service;

import org.springframework.transaction.annotation.Transactional;
import kr.bi.greenmate.dto.SignUpRequest;
import kr.bi.greenmate.dto.SignUpResponse;
import kr.bi.greenmate.entity.User;
import kr.bi.greenmate.exception.error.EmailDuplicateException;
import kr.bi.greenmate.exception.error.NicknameDuplicateException;
import kr.bi.greenmate.exception.error.FileUploadFailException;
import kr.bi.greenmate.exception.error.SignUpFailException;
import kr.bi.greenmate.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
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

        String profileImageUrl = null;
        MultipartFile profileImage = request.getProfileImage();

        if (profileImage != null && !profileImage.isEmpty()) {
            try {
                profileImageUrl = imageUploadService.upload(profileImage, "profile");
            } catch (Exception e) {
                throw new FileUploadFailException();
            }
        }

        User user = User.builder()
                .email(request.getEmail())
                .nickname(request.getNickname())
                .password(passwordEncoder.encode(request.getPassword()))
                .profileImageUrl(profileImageUrl)
                .selfIntroduction(request.getSelfIntroduction())
                .build();

        try {
            User savedUser = userRepository.save(user);
            return SignUpResponse.builder()
                    .userId(savedUser.getId())
                    .message("회원가입이 완료되었습니다.")
                    .build();
        } catch (DataIntegrityViolationException e) {
            if (userRepository.findByEmail(request.getEmail()).isPresent()) {
                throw new EmailDuplicateException();
            }
            if (userRepository.findByNickname(request.getNickname()).isPresent()) {
                throw new NicknameDuplicateException();
            }
            throw new SignUpFailException();
        }
    }
}
