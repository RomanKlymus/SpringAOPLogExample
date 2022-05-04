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

    @Pointcut("within(@org.springframework.web.bind.annotation.RestController *)")
    public void controller() {
    }

    @Pointcut("execution(* *.*(..))")
    public void allMethod() {
    }

    @Before("controller() && allMethod() && args(..,request)")
    public void logBefore(JoinPoint joinPoint, HttpServletRequest request) {
        log.info("Entering in Method :  " + joinPoint.getSignature().getName());
        log.info("Class Name :  " + joinPoint.getSignature().getDeclaringTypeName());
        log.info("Arguments :  " + Arrays.toString(joinPoint.getArgs()));
        log.info("Target class : " + joinPoint.getTarget().getClass().getName());

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

    @After("controller() && allMethod() && args(..,response)")
    public void logAfter(JoinPoint joinPoint, HttpServletResponse response) {
        log.info("Entering in Method :  " + joinPoint.getSignature().getName());
        log.info("Class Name :  " + joinPoint.getSignature().getDeclaringTypeName());
        log.info("Arguments :  " + Arrays.toString(joinPoint.getArgs()));
        log.info("Target class : " + joinPoint.getTarget().getClass().getName());

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

    @AfterReturning(pointcut = "controller() && allMethod()", returning = "result")
    public void logAfter(JoinPoint joinPoint, Object result) {
        String returnValue = this.getValue(result);
        log.info("Method Return value : " + returnValue);
    }

    @AfterThrowing(pointcut = "controller() && allMethod()", throwing = "exception")
    public void logAfterThrowing(JoinPoint joinPoint, Throwable exception) {
        log.error("An exception has been thrown in " + joinPoint.getSignature().getName() + " ()");
        log.error("Cause : " + exception.getCause());
    }

    @Around("controller() && allMethod()")
    public Object logAround(ProceedingJoinPoint joinPoint) throws Throwable {
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
            log.error("Illegal argument " + Arrays.toString(joinPoint.getArgs()) + " in "
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
