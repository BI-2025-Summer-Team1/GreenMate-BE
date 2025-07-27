package kr.bi.greenmate.exception.past;

public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException() {
        super("이메일 또는 비밀번호가 일치하지 않습니다.");
    }
}
