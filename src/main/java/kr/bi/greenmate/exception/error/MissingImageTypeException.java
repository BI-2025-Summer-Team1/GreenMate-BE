package kr.bi.greenmate.exception.error;

import kr.bi.greenmate.exception.BusinessException;
import kr.bi.greenmate.exception.ErrorCode;

public class MissingImageTypeException extends BusinessException {
	public MissingImageTypeException() {
		super(ErrorCode.MISSING_IMAGE_TYPE);
	}
} 
