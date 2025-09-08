package kr.bi.greenmate.exception.error;

import kr.bi.greenmate.exception.BusinessException;
import kr.bi.greenmate.exception.ErrorCode;

public class GeminiApiFailException extends BusinessException {
	public GeminiApiFailException() {
		super(ErrorCode.GEMINI_API_FAIL);
	}
}
