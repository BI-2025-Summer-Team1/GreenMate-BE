package kr.bi.greenmate.exception.error;

import kr.bi.greenmate.exception.BusinessException;
import kr.bi.greenmate.exception.ErrorCode;

public class ImageSizeExceedException extends BusinessException {
    public ImageSizeExceedException() {
        super(ErrorCode.IMAGE_SIZE_EXCEED);
    }
}
