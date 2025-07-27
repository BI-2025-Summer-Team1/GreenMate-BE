package kr.bi.greenmate.exception.past;

public class FileUploadFailException extends RuntimeException {
    public FileUploadFailException(){
        super("이미지 업로드에 실패했습니다.");
    }
}
