package kr.bi.greenmate.exception.error;

import kr.bi.greenmate.exception.BusinessException;
import kr.bi.greenmate.exception.ErrorCode;

public class RequiredAgreementsNotAcceptedException extends BusinessException {
	public RequiredAgreementsNotAcceptedException() {
		super(ErrorCode.REQUIRED_AGREEMENT_NOT_ACCEPTED);
	}
}
