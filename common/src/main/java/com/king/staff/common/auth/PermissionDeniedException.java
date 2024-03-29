package com.king.staff.common.auth;

import com.king.staff.common.api.ResultCode;
import lombok.Getter;

public class PermissionDeniedException extends RuntimeException {

    @Getter
    private final ResultCode resultCode;


    public PermissionDeniedException(String message) {
        super(message);
        this.resultCode=ResultCode.UN_AUTHORIZED;
    }

    public PermissionDeniedException(ResultCode resultCode) {
        super(resultCode.getMsg());
        this.resultCode = resultCode;
    }

    public PermissionDeniedException(Throwable cause, ResultCode resultCode) {
        super(cause);
        this.resultCode = resultCode;
    }

    @Override
    public Throwable fillInStackTrace(){
        return this;
    }
}
