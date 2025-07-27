package kr.bi.greenmate.exception.past;

public class InvalidImageTypeException extends RuntimeException {
    public InvalidImageTypeException(){
        super("잘못된 이미지 형식입니다.");
    }
}
