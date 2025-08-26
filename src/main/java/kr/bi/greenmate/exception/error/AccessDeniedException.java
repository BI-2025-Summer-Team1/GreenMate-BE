package kr.bi.greenmate.exception.error;

import kr.bi.greenmate.exception.BusinessException;
import kr.bi.greenmate.exception.ErrorCode;

public class AccessDeniedException extends BusinessException {
   
    public AccessDeniedException() {
        super(ErrorCode.ACCESS_DENIED);
    }
}
