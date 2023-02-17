package com.antra.aspect;

import com.alibaba.fastjson.JSON;
import com.antra.annotation.SystemLog;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

@Aspect
@Component
@Slf4j
public class LogAspect {

    // 用注解的方式来定义切点
    @Pointcut("@annotation(com.antra.annotation.SystemLog)")
    public void pointCut(){
    }

    // 定义通知方法
    @Around("pointCut()")
    public Object printLog(ProceedingJoinPoint joinPoint) throws Throwable{

        // 因为我最终都要打印日志信息，所以用finally

        Object ret;
        try {
            handleBefore(joinPoint);
            ret = joinPoint.proceed(); // 目标方法
            handleAfter(ret);
        } finally {
            // System.lineSeparator() 拼接的是系统的换行符
            log.info("=======End=======" + System.lineSeparator());
        }


        return ret;
    }

    private void handleAfter(Object ret) {
        // 打印出参
        log.info("Response       : {}", JSON.toJSON(ret));

    }

    private void handleBefore(ProceedingJoinPoint joinPoint) {
        // 获取request 对象， 强转成ServletRequestAttributes
        ServletRequestAttributes requestAttributes = (ServletRequestAttributes)RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = requestAttributes.getRequest();
        
        // 获取增强方法上的注解对象
        SystemLog systemLog = getSystemLog(joinPoint);

        log.info("=======Start=======");
        // 打印请求 URL: url其实是存在 request当中的
        log.info("URL            : {}", request.getRequestURL());
        // 打印描述信息: 接口的名字
        // @annotation里面指定属性, 定义一个getSystemLog()方法
        log.info("BusinessName   : {}", systemLog.businessName());
        // 打印 Http method
        log.info("HTTP Method    : {}", request.getMethod());
        // 打印调用 controller 的全路径以及执行方法
        log.info("Class Method   : {}.{}", joinPoint.getSignature().getDeclaringTypeName(), joinPoint.getSignature().getName());
        // 打印请求的 IP
        log.info("IP             : {}", request.getRemoteHost());
        // 打印请求入参
        log.info("Request Args   : {}", JSON.toJSON(joinPoint.getArgs()));
    }


    private SystemLog getSystemLog(ProceedingJoinPoint joinPoint) {
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        SystemLog systemLog = methodSignature.getMethod().getAnnotation(SystemLog.class);
        return systemLog;
    }

}
