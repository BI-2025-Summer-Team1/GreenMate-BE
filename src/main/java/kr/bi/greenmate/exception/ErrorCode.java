package kr.bi.greenmate.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {
    EMAIL_DUPLICATE("E01", "이미 사용 중인 이메일입니다.", HttpStatus.CONFLICT),
    NICKNAME_DUPLICATE("E02", "이미 사용 중인 닉네임입니다.", HttpStatus.CONFLICT),
    USER_NOT_FOUND("E03", "사용자를 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    FILE_EMPTY("E04", "업로드할 파일이 없습니다.", HttpStatus.BAD_REQUEST),
    INVALID_IMAGE_TYPE("E05", "지원하지 않는 이미지 형식입니다.", HttpStatus.BAD_REQUEST),
    MISSING_IMAGE_TYPE("E06", "이미지 타입이 누락되었습니다.", HttpStatus.BAD_REQUEST),
    FILE_UPLOAD_FAIL("E07", "파일 업로드에 실패했습니다.", HttpStatus.INTERNAL_SERVER_ERROR),
    SIGNUP_FAIL("E08", "회원가입 처리 중 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR);

    private final String code;
    private final String message;
    private final HttpStatus status;

    ErrorCode(String code, String message, HttpStatus status) {
        this.code = code;
        this.message = message;
        this.status = status;
    }
} 
