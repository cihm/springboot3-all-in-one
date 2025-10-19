package com.atomicjar.todos.metrics;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class UnifiedExceptionMonitoringAspect {

    private final PrometheusExceptionMonitor exceptionMonitor;
    private final DatabaseFailureMetrics dbFailureMetrics;

    public UnifiedExceptionMonitoringAspect(PrometheusExceptionMonitor exceptionMonitor,
                                            DatabaseFailureMetrics dbFailureMetrics) {
        this.exceptionMonitor = exceptionMonitor;
        this.dbFailureMetrics = dbFailureMetrics;
    }
//merge global exception handler and exception monitoring aspect
//    @Around("execution(* com.atomicjar.todos.config..*(..)) || execution(* com.atomicjar.todos.repository..*(..)) || execution(* com.atomicjar.todos.service..*(..))")
//    public Object monitorExceptions(ProceedingJoinPoint joinPoint) throws Throwable {
//        try {
//            return joinPoint.proceed();
//        } catch (Exception ex) {
//            String packageName = joinPoint.getSignature().getDeclaringTypeName();
//
//            if (packageName.startsWith("com.atomicjar.todos.repository")) {
//                // 計數資料庫相關錯誤
//                dbFailureMetrics.incrementFailure();
//            } else {
//                // 其他異常計數
//                exceptionMonitor.incrementExceptionCount();
//            }
//
//            throw ex; // 保持異常繼續拋出
//        }
//    }
}
