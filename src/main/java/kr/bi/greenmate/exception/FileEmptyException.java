package kr.bi.greenmate.exception;

public class FileEmptyException extends RuntimeException {
    public FileEmptyException() {
        super("업로드할 파일이 없습니다.");
    }
}
