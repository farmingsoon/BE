package com.api.farmingsoon.common.exception.custom_exception;


import com.api.farmingsoon.common.exception.CustomException;
import com.api.farmingsoon.common.exception.ErrorCode;

public class AwsS3Exception extends CustomException {
    public AwsS3Exception(ErrorCode errorCode) {
        super(errorCode);
    }
}