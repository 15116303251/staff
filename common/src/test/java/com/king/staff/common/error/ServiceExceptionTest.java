package com.king.staff.common.error;

import com.king.staff.common.api.ResultCode;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class ServiceExceptionTest {

    @Test
    public void testServiceExceptionWithMessage() {
        String message = "Test error message";
        ServiceException exception = new ServiceException(message);
        
        assertThat(exception.getMessage()).isEqualTo(message);
        assertThat(exception.getResultCode()).isEqualTo(ResultCode.FAILURE);
    }

    @Test
    public void testServiceExceptionWithResultCode() {
        ResultCode resultCode = ResultCode.NOT_FOUND;
        ServiceException exception = new ServiceException(resultCode);
        
        assertThat(exception.getMessage()).isEqualTo(resultCode.getMsg());
        assertThat(exception.getResultCode()).isEqualTo(resultCode);
    }

    @Test
    public void testServiceExceptionWithMessageAndResultCode() {
        String message = "Custom error message";
        ResultCode resultCode = ResultCode.PARAM_VALID_ERROR;
        ServiceException exception = new ServiceException(message, resultCode);
        
        assertThat(exception.getMessage()).isEqualTo(message);
        assertThat(exception.getResultCode()).isEqualTo(resultCode);
    }

    @Test
    public void testServiceExceptionWithCauseAndResultCode() {
        Throwable cause = new RuntimeException("Root cause");
        ResultCode resultCode = ResultCode.INTERNAL_SERVER_ERROR;
        ServiceException exception = new ServiceException(cause, resultCode);
        
        assertThat(exception.getCause()).isEqualTo(cause);
        assertThat(exception.getResultCode()).isEqualTo(resultCode);
        assertThat(exception.getMessage()).isEqualTo(resultCode.getMsg());
    }

    @Test
    public void testServiceExceptionWithMessageAndCause() {
        String message = "Error with cause";
        Throwable cause = new IllegalArgumentException("Invalid argument");
        ServiceException exception = new ServiceException(message, cause);
        
        assertThat(exception.getMessage()).isEqualTo(message);
        assertThat(exception.getCause()).isEqualTo(cause);
        assertThat(exception.getResultCode()).isEqualTo(ResultCode.FAILURE);
    }

    @Test
    public void testFillInStackTrack() {
        ServiceException exception = new ServiceException("Test");
        Throwable result = exception.fillInStackTrack();
        
        assertThat(result).isEqualTo(exception);
    }

    @Test
    public void testDoFillInStackTrack() {
        ServiceException exception = new ServiceException("Test");
        Throwable result = exception.doFillInStackTrack();
        
        assertThat(result).isNotNull();
        assertThat(result).isNotEqualTo(exception);
    }
}