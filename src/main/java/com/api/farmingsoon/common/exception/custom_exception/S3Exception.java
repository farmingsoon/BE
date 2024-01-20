package com.api.farmingsoon.common.exception.custom_exception;


import com.api.farmingsoon.common.exception.CustomException;
import com.api.farmingsoon.common.exception.ErrorCode;

public class S3Exception extends CustomException {
    public S3Exception(ErrorCode errorCode) {
        super(errorCode);
    }
}