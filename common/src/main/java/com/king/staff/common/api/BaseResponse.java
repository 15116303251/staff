package com.king.staff.common.api;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author 旺
 * @description 响应回复类
 * @date 2019-11-02
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BaseResponse {

    private String message;

    @Builder.Default
    private ResultCode code=ResultCode.SUCCESS;

    public boolean isSuccess(){
        return code==ResultCode.SUCCESS;
    }

}
