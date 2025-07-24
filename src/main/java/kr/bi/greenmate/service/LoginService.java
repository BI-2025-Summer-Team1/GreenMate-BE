package kr.bi.greenmate.service;

import kr.bi.greenmate.config.JwtProvider;
import kr.bi.greenmate.dto.LoginRequest;
import kr.bi.greenmate.dto.LoginResponse;
import kr.bi.greenmate.entity.User;
import kr.bi.greenmate.exception.UserNotFoundException;
import kr.bi.greenmate.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class LoginService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;

    @Transactional(readOnly = true)
    public LoginResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail()).orElseThrow(UserNotFoundException::new);

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new UserNotFoundException();
        }

        String accessToken = jwtProvider.createToken(user.getId(), user.getEmail(), user.getNickname());

        return LoginResponse.builder()
                .accessToken(accessToken)
                .userId(user.getId())
                .nickname(user.getNickname())
                .build();
    }
}
