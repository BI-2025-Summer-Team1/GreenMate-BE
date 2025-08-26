package kr.bi.greenmate.exception.error;

import kr.bi.greenmate.exception.BusinessException;
import kr.bi.greenmate.exception.ErrorCode;

public class InvalidImageTypeException extends BusinessException {
	public InvalidImageTypeException() {
		super(ErrorCode.INVALID_IMAGE_TYPE);
	}
} 
