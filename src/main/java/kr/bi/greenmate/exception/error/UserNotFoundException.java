package kr.bi.greenmate.exception.error;

import kr.bi.greenmate.exception.BusinessException;
import kr.bi.greenmate.exception.ErrorCode;

public class UserNotFoundException extends BusinessException {
	public UserNotFoundException() {
		super(ErrorCode.USER_NOT_FOUND);
	}
} 
