package kr.bi.greenmate.exception.error;

import kr.bi.greenmate.exception.BusinessException;
import kr.bi.greenmate.exception.ErrorCode;

public class RedisConnectionFailException extends BusinessException {
	public RedisConnectionFailException() {
		super(ErrorCode.REDIS_CONNECTION_FAIL);
	}
}
