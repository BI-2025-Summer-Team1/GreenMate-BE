package kr.bi.greenmate.exception;

import org.springframework.http.HttpStatus;

import lombok.Getter;

@Getter
public enum ErrorCode {
    EMAIL_DUPLICATE("E01", "이미 사용 중인 이메일입니다.", HttpStatus.CONFLICT),
    NICKNAME_DUPLICATE("E02", "이미 사용 중인 닉네임입니다.", HttpStatus.CONFLICT),
    USER_NOT_FOUND("E03", "사용자를 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    FILE_EMPTY("E04", "업로드할 파일이 없습니다.", HttpStatus.BAD_REQUEST),
    INVALID_IMAGE_TYPE("E05", "지원하지 않는 이미지 형식입니다.", HttpStatus.BAD_REQUEST),
    MISSING_IMAGE_TYPE("E06", "이미지 타입이 누락되었습니다.", HttpStatus.BAD_REQUEST),
    FILE_UPLOAD_FAIL("E07", "파일 업로드에 실패했습니다.", HttpStatus.INTERNAL_SERVER_ERROR),
    SIGNUP_FAIL("E08", "회원가입 처리 중 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR),
    IMAGE_COUNT_EXCEED("E09", "이미지는 최대 10개까지 업로드 가능합니다.", HttpStatus.BAD_REQUEST),
    IMAGE_SIZE_EXCEED("E10", "이미지 용량은 1MB를 초과할 수 없습니다.", HttpStatus.BAD_REQUEST),
    POST_NOT_FOUND("E11", "게시글을 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    RECRUITMENT_POST_NOT_FOUND("E21", "해당 ID의 모집 게시물을 찾을 수 없습니다.", HttpStatus.NOT_FOUND);

    private final String code;
    private final String message;
    private final HttpStatus status;

    ErrorCode(String code, String message, HttpStatus status) {
        this.code = code;
        this.message = message;
        this.status = status;
    }
} 
