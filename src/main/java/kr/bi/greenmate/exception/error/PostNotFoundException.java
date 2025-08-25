package kr.bi.greenmate.exception.error;

import kr.bi.greenmate.exception.BusinessException;
import kr.bi.greenmate.exception.ErrorCode;

public class PostNotFoundException extends BusinessException {
	public PostNotFoundException() {
		super(ErrorCode.POST_NOT_FOUND);
	}
}
