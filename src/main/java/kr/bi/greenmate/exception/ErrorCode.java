package kr.bi.greenmate.exception;

import org.springframework.http.HttpStatus;

import lombok.Getter;

@Getter
public enum ErrorCode {
    FILE_EMPTY("400-01", "업로드할 파일이 없습니다.", HttpStatus.BAD_REQUEST),
    INVALID_IMAGE_TYPE("400-02", "지원하지 않는 이미지 형식입니다.", HttpStatus.BAD_REQUEST),
    MISSING_IMAGE_TYPE("400-03", "이미지 타입이 누락되었습니다.", HttpStatus.BAD_REQUEST),
    IMAGE_COUNT_EXCEED("400-04", "이미지는 최대 10개까지 업로드 가능합니다.", HttpStatus.BAD_REQUEST),
    IMAGE_SIZE_EXCEED("400-05", "이미지 용량은 1MB를 초과할 수 없습니다.", HttpStatus.BAD_REQUEST),
    PARENT_COMMENT_MISMATCH("400-06", "부모 댓글이 해당 게시글에 속하지 않습니다.", HttpStatus.BAD_REQUEST),

    USER_NOT_FOUND("404-01", "사용자를 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    POST_NOT_FOUND("404-02", "게시글을 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    RECRUITMENT_POST_NOT_FOUND("404-03", "해당 ID의 모집 게시물을 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    COMMENT_NOT_FOUND("404-04", "댓글을 찾을 수 없습니다.", HttpStatus.NOT_FOUND),

    EMAIL_DUPLICATE("409-01", "이미 사용 중인 이메일입니다.", HttpStatus.CONFLICT),
    NICKNAME_DUPLICATE("409-02", "이미 사용 중인 닉네임입니다.", HttpStatus.CONFLICT),
    CONCURRENT_LIKE_FAIL("409-03", "동시성 문제로 좋아요 처리에 실패했습니다.", HttpStatus.CONFLICT),

    FILE_UPLOAD_FAIL("500-01", "파일 업로드에 실패했습니다.", HttpStatus.INTERNAL_SERVER_ERROR),
    SIGNUP_FAIL("500-02", "회원가입 처리 중 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR);

    private final String code;
    private final String message;
    private final HttpStatus status;

    ErrorCode(String code, String message, HttpStatus status) {
        this.code = code;
        this.message = message;
        this.status = status;
    }
} 
