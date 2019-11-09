package com.king.staff.common.error;

import com.github.structlog4j.ILogger;
import com.github.structlog4j.SLoggerFactory;
import com.king.staff.common.api.BaseResponse;
import com.king.staff.common.api.ResultCode;
import com.king.staff.common.auth.PermissionDeniedException;
import org.hibernate.validator.internal.engine.path.PathImpl;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.NoHandlerFoundException;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.util.Set;

@RestControllerAdvice
public class GlobalExceptionTranslator {

    static final ILogger logger = SLoggerFactory.getLogger(GlobalExceptionTranslator.class);

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public BaseResponse handleError(MissingServletRequestParameterException e){
        logger.warn("Missing Request Parameter",e);
        String message =String.format("Missing Request Parameter:%s",e.getParameterName());
        return BaseResponse.builder().code(ResultCode.PARAM_MISS)
                .message(message)
                .build();
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public BaseResponse handleError(MethodArgumentNotValidException e){
        logger.warn("Method Argument Not Valid",e);
        BindingResult result=e.getBindingResult();
        FieldError error=result.getFieldError();
        String message=String.format("%s:%s",error.getField(),error.getDefaultMessage());
        return BaseResponse.builder()
                .code(ResultCode.PARAM_VALID_ERROR)
                .message(message)
                .build();
    }

    @ExceptionHandler(BindException.class)
    public BaseResponse handleError(BindException e){
        logger.warn("Bind Exception" ,e);
        FieldError error=e.getFieldError();
        String message=String.format("%s:%s",error.getField(),error.getDefaultMessage());
        return BaseResponse.builder()
                .code(ResultCode.PARAM_BIND_ERROR)
                .message(message)
                .build();
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public BaseResponse handleError(ConstraintViolationException e){
        logger.warn("Constraint Violation" ,e);
        Set<ConstraintViolation<?>> violations=e.getConstraintViolations();
        ConstraintViolation<?> violation=violations.iterator().next();
        String path=((PathImpl)violation.getPropertyPath()).getLeafNode().getName();
        String message=String.format("%s:%s",path,violation.getMessage());
        return BaseResponse.builder()
                .code(ResultCode.PARAM_VALID_ERROR)
                .message(message)
                .build();
    }

    @ExceptionHandler(NoHandlerFoundException.class)
    public BaseResponse handleError(NoHandlerFoundException e){
        logger.warn("404 Not Found" ,e);
        return BaseResponse.builder()
                .code(ResultCode.NOT_FOUND)
                .message(e.getMessage())
                .build();
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public BaseResponse handleError(HttpMessageNotReadableException e){
        logger.warn("Message Not Readable" ,e);
        return BaseResponse.builder()
                .code(ResultCode.MSG_NOT_Readable)
                .message(e.getMessage())
                .build();
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public BaseResponse handleError(HttpRequestMethodNotSupportedException e){
        logger.warn("Request Method Not Supported" ,e);
        return BaseResponse.builder()
                .code(ResultCode.METHOD_NOT_SUPPORTED)
                .message(e.getMessage())
                .build();
    }


    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public BaseResponse handleError(HttpMediaTypeNotSupportedException e){
        logger.warn("Method Type Not Supported" ,e);
        return BaseResponse.builder()
                .code(ResultCode.MEDIA_TYPE_NOT_SUPPORTED)
                .message(e.getMessage())
                .build();
    }

    @ExceptionHandler(ServiceException.class)
    public BaseResponse handleError(ServiceException e){
        logger.warn("Service Exception" ,e);
        return BaseResponse.builder()
                .code(e.getResultCode())
                .message(e.getMessage())
                .build();
    }

    @ExceptionHandler(PermissionDeniedException.class)
    public BaseResponse handleError(PermissionDeniedException e){
        logger.warn("Permission Denied" ,e);
        return BaseResponse.builder()
                .code(e.getResultCode())
                .message(e.getMessage())
                .build();
    }

    @ExceptionHandler(Throwable.class)
    public BaseResponse handleError(Throwable e){
        logger.warn("Internal Server Error" ,e);
        return BaseResponse.builder()
                .code(ResultCode.INTERNAL_SERVER_ERROR)
                .message(e.getMessage())
                .build();
    }
}
