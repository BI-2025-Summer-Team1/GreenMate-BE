package kr.bi.greenmate.exception.error;

import kr.bi.greenmate.exception.BusinessException;
import kr.bi.greenmate.exception.ErrorCode;

public class PostDeletionException extends BusinessException {
	public PostDeletionException() {
		super(ErrorCode.POST_DELETION_FAILED);
	}
}
