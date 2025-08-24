package kr.bi.greenmate.exception.error;

import kr.bi.greenmate.exception.BusinessException;
import kr.bi.greenmate.exception.ErrorCode;

public class ViewCountPersistFailException extends BusinessException {
	public ViewCountPersistFailException() {
		super(ErrorCode.VIEW_COUNT_PERSIST_FAIL);
	}
}
