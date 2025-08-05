package kr.bi.greenmate.exception.error;

import kr.bi.greenmate.exception.BusinessException;
import kr.bi.greenmate.exception.ErrorCode;

public class RecruitmentPostNotFoundException extends BusinessException {

    public RecruitmentPostNotFoundException(Long postId) {
        super(ErrorCode.RECRUITMENT_POST_NOT_FOUND, "Recruitment post with id " + postId + " not found.");
    }
}
