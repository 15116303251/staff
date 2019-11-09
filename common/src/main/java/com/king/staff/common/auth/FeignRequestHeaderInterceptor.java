package com.king.staff.common.auth;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import io.micrometer.core.instrument.util.StringUtils;

/**
 * @author 旺
 * @description 传输认证后的信息到后台
 * @date 2019-11-02
 */
public class FeignRequestHeaderInterceptor implements RequestInterceptor {

    @Override
    public void apply(RequestTemplate requestTemplate) {
        String userId =AuthContext.getUserId();
        if(!StringUtils.isEmpty(userId)){
            requestTemplate.header(AuthConstant.CURRENT_USER_HEADER,userId);
        }
    }
}
