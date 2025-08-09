package kr.bi.greenmate.exception.error;

import kr.bi.greenmate.exception.BusinessException;
import kr.bi.greenmate.exception.ErrorCode;

public class OptimisticLockCustomException extends BusinessException {
    public OptimisticLockCustomException() {
        super(ErrorCode.CONCURRENT_LIKE_FAIL);
    }
}
