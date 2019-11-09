package com.king.staff.common.auth;

import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

/**
 * @author 旺
 * @decription 一个用于保存当前用户id以及认证信息的类
 * @date 2019-11-02
 */
public class AuthContext {

    /**
     * 根据参数值从请求头中获取响应的值
     * @param headerName
     * @return
     */
    private static String getRequestHeader(String headerName){
        RequestAttributes requestAttributes= RequestContextHolder.getRequestAttributes();
        if(requestAttributes instanceof ServletRequestAttributes){
            HttpServletRequest request=((ServletRequestAttributes) requestAttributes).getRequest();
            String value=request.getHeader(headerName);
            return value;
        }
        return null;
    }

    /**
     * 获取当前请求头中携带的用户id
     * @return
     */
    public static String getUserId(){
        return getRequestHeader(AuthConstant.CURRENT_USER_HEADER);
    }

    /**
     * 获取当前请求头中携带的用户认证信息
     * @return
     */
    public static String getAuthz(){
        return getRequestHeader(AuthConstant.AUTHORIZATION_HEADER);
    }
}
