package com.aopexample;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;

@Aspect
@Component
public class LogAspect {

    Logger log = LoggerFactory.getLogger(this.getClass());

    @Pointcut("@within(org.springframework.web.bind.annotation.RestController)")
    public void controller() {
    }

    @Before("controller() && args(..,request)")
    public void logBefore(JoinPoint joinPoint, HttpServletRequest request) {
        log.info("=== LOG START ===");
        log.info("Entering in Method :  " + joinPoint.getSignature().getName());
        log.info("Class Name :  " + joinPoint.getSignature().getDeclaringTypeName());
        log.info("Arguments :  " + Arrays.toString(joinPoint.getArgs()));

        if (null != request) {
            log.info("Start Header Section of request ");
            log.info("Method Type : " + request.getMethod());
            Enumeration<String> headerNames = request.getHeaderNames();
            while (headerNames.hasMoreElements()) {
                String headerName = headerNames.nextElement();
                String headerValue = request.getHeader(headerName);
                log.info("Header Name: " + headerName + " Header Value : " + headerValue);
            }
            log.info("Request Path info :" + request.getServletPath());
            log.info("End Header Section of request ");
        }
    }

    @After("controller() && args(..,response)")
    public void logAfter(JoinPoint joinPoint, HttpServletResponse response) {
        log.info("=== LOG START ===");
        log.info("Entering in Method :  " + joinPoint.getSignature().getName());
        log.info("Class Name :  " + joinPoint.getSignature().getDeclaringTypeName());
        log.info("Arguments :  " + Arrays.toString(joinPoint.getArgs()));

        if (null != response) {
            response.setHeader(HttpHeaders.SET_COOKIE, "Delicious cookie");
            log.info("Start Header Section of response ");
            Enumeration<String> headerNames = Collections.enumeration(response.getHeaderNames());
            while (headerNames.hasMoreElements()) {
                String headerName = headerNames.nextElement();
                String headerValue = response.getHeader(headerName);
                log.info("Header Name: " + headerName + " Header Value : " + headerValue);
            }
            log.info("End Header Section of response ");
        }
    }

    @AfterReturning(pointcut = "controller()", returning = "result")
    public void logAfter(JoinPoint joinPoint, Object result) {
        log.info("=== LOG START ===");
        String returnValue = this.getValue(result);
        log.info("Method Return value : " + returnValue);
    }

    @AfterThrowing(pointcut = "controller()", throwing = "exception")
    public void logAfterThrowing(JoinPoint joinPoint, Throwable exception) {
        log.info("=== LOG START ===");
        log.error("An exception has been thrown in " + joinPoint.getSignature().getName() + " ()");
        log.error("Cause : " + exception.getCause());
    }

    @Around("controller()")
    public Object logAround(ProceedingJoinPoint joinPoint) throws Throwable {
        log.info("=== LOG START ===");
        long start = System.currentTimeMillis();
        try {
            String className = joinPoint.getSignature().getDeclaringTypeName();
            String methodName = joinPoint.getSignature().getName();
            Object result = joinPoint.proceed();
            long elapsedTime = System.currentTimeMillis() - start;
            log.info("Method " + className + "." + methodName + " ()" + " execution time : "
                    + elapsedTime + " ms");

            return result;
        } catch (IllegalArgumentException e) {
            log.info("Illegal argument " + Arrays.toString(joinPoint.getArgs()) + " in "
                    + joinPoint.getSignature().getName() + "()");
            throw e;
        }
    }

    private String getValue(Object result) {
        String returnValue = null;
        if (null != result) {
            if (result.toString().endsWith("@" + Integer.toHexString(result.hashCode()))) {
                returnValue = ReflectionToStringBuilder.toString(result);
            } else {
                returnValue = result.toString();
            }
        }
        return returnValue;
    }
}
