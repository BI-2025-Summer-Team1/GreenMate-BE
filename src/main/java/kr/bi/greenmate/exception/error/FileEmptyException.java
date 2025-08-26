package kr.bi.greenmate.exception.error;

import kr.bi.greenmate.exception.BusinessException;
import kr.bi.greenmate.exception.ErrorCode;

public class FileEmptyException extends BusinessException {
	public FileEmptyException() {
		super(ErrorCode.FILE_EMPTY);
	}
} 
