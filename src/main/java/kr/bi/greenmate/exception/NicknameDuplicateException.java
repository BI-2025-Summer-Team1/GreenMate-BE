package kr.bi.greenmate.exception;

public class NicknameDuplicateException extends RuntimeException{
    public NicknameDuplicateException() {
        super("이미 사용 중인 닉네임입니다.");
    }
}
