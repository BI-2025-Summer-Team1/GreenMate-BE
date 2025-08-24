package kr.bi.greenmate.exception.error;

import kr.bi.greenmate.exception.BusinessException;
import kr.bi.greenmate.exception.ErrorCode;

public class FileUploadFailException extends BusinessException {
	public FileUploadFailException() {
		super(ErrorCode.FILE_UPLOAD_FAIL);
	}
} 
