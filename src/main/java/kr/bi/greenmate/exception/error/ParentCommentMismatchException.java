package kr.bi.greenmate.exception.error;

import kr.bi.greenmate.exception.BusinessException;
import kr.bi.greenmate.exception.ErrorCode;

public class ParentCommentMismatchException extends BusinessException {

	public ParentCommentMismatchException() {
		super(ErrorCode.PARENT_COMMENT_MISMATCH);
	}
}
