package kr.bi.greenmate.exception.error;

import kr.bi.greenmate.exception.BusinessException;
import kr.bi.greenmate.exception.ErrorCode;

public class ImageCountExceedException extends BusinessException {
    public ImageCountExceedException() {
        super(ErrorCode.IMAGE_COUNT_EXCEED);
    }
}
