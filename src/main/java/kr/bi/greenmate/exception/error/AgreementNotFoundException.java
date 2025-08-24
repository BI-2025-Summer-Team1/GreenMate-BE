package kr.bi.greenmate.exception.error;

import kr.bi.greenmate.exception.BusinessException;
import kr.bi.greenmate.exception.ErrorCode;

public class AgreementNotFoundException extends BusinessException {
	public AgreementNotFoundException() {
		super(ErrorCode.AGREEMENT_NOT_FOUND);
	}
}
