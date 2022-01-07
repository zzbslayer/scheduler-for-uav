package com.github.zzbslayer.vnfdemo.aop;

import com.github.zzbslayer.vnfdemo.service.AccessMetricService;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

@Slf4j
@Aspect
@Order(1)
@Component
public class MetricAop {
    @Autowired
    AccessMetricService accessMetricService;


    @Pointcut("execution(public * com.github.zzbslayer.vnfdemo.controller..*(..))")
    public void updateMetric() {
    }

    @Around("updateMetric()")
    public Object handlerController(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        accessMetricService.increaseAccess();
        return proceedingJoinPoint.proceed();
    }
}
