package com.king.staff.common.api;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class BaseResponseTest {

    @Test
    public void testDefaultConstructor() {
        BaseResponse response = new BaseResponse();
        
        assertThat(response.getMessage()).isNull();
        assertThat(response.getCode()).isEqualTo(ResultCode.SUCCESS);
        assertThat(response.isSuccess()).isTrue();
    }

    @Test
    public void testAllArgsConstructor() {
        String message = "Test message";
        ResultCode code = ResultCode.NOT_FOUND;
        
        BaseResponse response = new BaseResponse(message, code);
        
        assertThat(response.getMessage()).isEqualTo(message);
        assertThat(response.getCode()).isEqualTo(code);
        assertThat(response.isSuccess()).isFalse();
    }

    @Test
    public void testBuilder() {
        String message = "Builder message";
        ResultCode code = ResultCode.SUCCESS;
        
        BaseResponse response = BaseResponse.builder()
                .message(message)
                .code(code)
                .build();
        
        assertThat(response.getMessage()).isEqualTo(message);
        assertThat(response.getCode()).isEqualTo(code);
        assertThat(response.isSuccess()).isTrue();
    }

    @Test
    public void testBuilderWithDefaultCode() {
        String message = "Message only";
        
        BaseResponse response = BaseResponse.builder()
                .message(message)
                .build();
        
        assertThat(response.getMessage()).isEqualTo(message);
        assertThat(response.getCode()).isEqualTo(ResultCode.SUCCESS);
        assertThat(response.isSuccess()).isTrue();
    }

    @Test
    public void testIsSuccessWithSuccessCode() {
        BaseResponse response = BaseResponse.builder()
                .code(ResultCode.SUCCESS)
                .build();
        
        assertThat(response.isSuccess()).isTrue();
    }

    @Test
    public void testIsSuccessWithFailureCode() {
        BaseResponse response = BaseResponse.builder()
                .code(ResultCode.FAILURE)
                .build();
        
        assertThat(response.isSuccess()).isFalse();
    }

    @Test
    public void testIsSuccessWithOtherCode() {
        BaseResponse response = BaseResponse.builder()
                .code(ResultCode.INTERNAL_SERVER_ERROR)
                .build();
        
        assertThat(response.isSuccess()).isFalse();
    }
}