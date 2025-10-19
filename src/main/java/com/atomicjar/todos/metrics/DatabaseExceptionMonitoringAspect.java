package com.atomicjar.todos.metrics;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.CannotCreateTransactionException;

@Aspect
@Component
public class DatabaseExceptionMonitoringAspect {

    private final DatabaseFailureMetrics databaseFailureMetrics;

    public DatabaseExceptionMonitoringAspect(DatabaseFailureMetrics databaseFailureMetrics) {
        this.databaseFailureMetrics = databaseFailureMetrics;
    }

    // 攔截 repository 包以及其他可能的資料存取位置方法
    @Around("execution(* com.atomicjar.todos.repository..*(..)) ")
    public Object monitorDatabaseExceptions(ProceedingJoinPoint joinPoint) throws Throwable {
        try {
            return joinPoint.proceed();
        } catch (Exception ex) {
            if (ex instanceof DataAccessException
                    || ex instanceof CannotCreateTransactionException
                    || (ex.getCause() != null && ex.getCause() instanceof java.sql.SQLException)) {
                databaseFailureMetrics.incrementFailure();
            }
            throw ex;
        }
    }
}
