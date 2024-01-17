package com.api.farmingsoon.common.exception.custom_exception;

import com.api.farmingsoon.common.exception.CustomException;
import com.api.farmingsoon.common.exception.ErrorCode;

public class InvalidException extends CustomException {
    public InvalidException(ErrorCode errorCode) {
        super(errorCode);
    }
}
