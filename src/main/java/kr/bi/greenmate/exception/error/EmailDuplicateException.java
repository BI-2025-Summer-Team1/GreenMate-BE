package kr.bi.greenmate.exception.error;

import kr.bi.greenmate.exception.BusinessException;
import kr.bi.greenmate.exception.ErrorCode;

public class EmailDuplicateException extends BusinessException {
	public EmailDuplicateException() {
		super(ErrorCode.EMAIL_DUPLICATE);
	}
} 
