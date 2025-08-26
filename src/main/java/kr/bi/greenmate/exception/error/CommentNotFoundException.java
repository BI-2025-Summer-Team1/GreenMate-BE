package kr.bi.greenmate.exception.error;

import kr.bi.greenmate.exception.BusinessException;
import kr.bi.greenmate.exception.ErrorCode;

public class CommentNotFoundException extends BusinessException {

	public CommentNotFoundException() {
		super(ErrorCode.COMMENT_NOT_FOUND);
	}
}
