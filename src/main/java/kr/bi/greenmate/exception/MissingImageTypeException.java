package kr.bi.greenmate.exception;

public class MissingImageTypeException extends RuntimeException {
    public MissingImageTypeException(){
        super("업로드 타입이 올바르지 않습니다.");
    }
}
