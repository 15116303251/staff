package com.king.staff.common.auth;

import com.king.staff.common.crypto.Sign;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

public class Sessions {

    public static final long SHORT_SESSION= TimeUnit.HOURS.toMillis(12);
    public static final long LONG_SESSION= TimeUnit.HOURS.toMillis(30*24);

    /**
     * 生成请求返回头中的token
     * @param userId 用户id
     * @param support
     * @param rememberMe 是否记住
     * @param signingSecret 密钥
     * @param externalApex
     * @param reponse
     */
    public static void loginUser(String userId, boolean support,
                                 boolean rememberMe, String signingSecret, String externalApex, HttpServletResponse reponse){
        long duration;
        int maxAge;

        if(rememberMe){
            //remember me
            duration=LONG_SESSION;
        }else{
            duration=SHORT_SESSION;
        }
        maxAge=(int)(duration/1000);

        String token= Sign.generateSessionToken(userId,signingSecret,support,duration);

        Cookie cookie =new Cookie(AuthConstant.COOKIE_NAME,token);
        cookie.setPath("/");
        cookie.setDomain(externalApex);
        cookie.setMaxAge(maxAge);
        cookie.setHttpOnly(true);
        reponse.addCookie(cookie);
    }

    /**
     * @description 获取token中存储的值
     * @param request
     * @return
     */
    public static String getToken(HttpServletRequest request){
        Cookie[] cookies=request.getCookies();
        if(cookies!=null || cookies.length==0){
            return null;
        }
        Cookie tokenCookie = Arrays.stream(cookies)
                .filter(cookie -> AuthConstant.COOKIE_NAME.equals(cookie.getName()))
                .findAny().orElse(null);

        if(tokenCookie==null){
            return null;
        }
        return tokenCookie.getValue();
    }

    /**
     * @description 清除cookie中的值
     * @param externalApex
     * @param response
     */
    public static void loout(String externalApex,HttpServletResponse response){
        Cookie cookie=new Cookie(AuthConstant.COOKIE_NAME,"");
        cookie.setPath("/");
        cookie.setMaxAge(0);
        cookie.setDomain(externalApex);
        response.addCookie(cookie);
    }
}
