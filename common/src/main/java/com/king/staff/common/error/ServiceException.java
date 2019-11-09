package com.king.staff.common.error;

import com.king.staff.common.api.ResultCode;
import lombok.Getter;

/**
 * @author 旺
 * @description 服务异常类
 * @date 2019-11-09
 */
public class ServiceException extends RuntimeException{

    @Getter
    private final ResultCode resultCode;

    public ServiceException(String message) {
        super(message);
        this.resultCode = ResultCode.FAILURE;
    }

    public ServiceException(ResultCode resultCode) {
        super(resultCode.getMsg());
        this.resultCode = resultCode;
    }

    public ServiceException(String message, ResultCode resultCode) {
        super(message);
        this.resultCode = resultCode;
    }

    public ServiceException(Throwable cause,ResultCode resultCode) {
        super(cause);
        this.resultCode = resultCode;
    }

    public ServiceException(String message,Throwable cause){
        super(message,cause);
        this.resultCode=ResultCode.FAILURE;
    }

    public Throwable fillInStackTrack(){
        return this;
    }

    public Throwable doFillInStackTrack(){
        return super.fillInStackTrace();
    }
}
