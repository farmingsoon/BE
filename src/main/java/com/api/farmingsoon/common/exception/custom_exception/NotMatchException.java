package com.api.farmingsoon.common.exception.custom_exception;

import com.api.farmingsoon.common.exception.CustomException;
import com.api.farmingsoon.common.exception.ErrorCode;

public class NotMatchException extends CustomException {
    public NotMatchException(ErrorCode errorCode) {
        super(errorCode);
    }
}
