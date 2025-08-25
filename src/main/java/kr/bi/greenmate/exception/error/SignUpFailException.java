package kr.bi.greenmate.exception.error;

import kr.bi.greenmate.exception.BusinessException;
import kr.bi.greenmate.exception.ErrorCode;

public class SignUpFailException extends BusinessException {
	public SignUpFailException() {
		super(ErrorCode.SIGNUP_FAIL);
	}
}
