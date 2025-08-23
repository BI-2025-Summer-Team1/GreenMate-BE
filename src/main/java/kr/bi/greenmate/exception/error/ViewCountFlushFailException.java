package kr.bi.greenmate.exception.error;

import kr.bi.greenmate.exception.BusinessException;
import kr.bi.greenmate.exception.ErrorCode;

public class ViewCountFlushFailException extends BusinessException {
    public ViewCountFlushFailException() {
        super(ErrorCode.VIEW_COUNT_FLUSH_FAIL);
    }
}
