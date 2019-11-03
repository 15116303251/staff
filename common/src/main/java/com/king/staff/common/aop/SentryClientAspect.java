package com.king.staff.common.aop;
import com.king.staff.common.env.EnvConfig;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author shenwang
 * @description 切面判断是否处于调试模式，若是，则不记录日志，否则记录日志
 * @date 2019-11-02
 */
@Slf4j
@Aspect
public class SentryClientAspect {

    @Autowired
    EnvConfig envConfig;

    @Around("execution(* io.sentry.SentryClient.send*(..))")
    public void around(ProceedingJoinPoint joinPoint)throws Throwable{
//      no sentry logging in debug mode
        if(envConfig.isDebug()){
            log.debug("no sentry logging in debug model");
            return;
        }
        joinPoint.proceed();
    }
}
