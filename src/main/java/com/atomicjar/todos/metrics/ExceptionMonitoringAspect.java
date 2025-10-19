package com.atomicjar.todos.metrics;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class ExceptionMonitoringAspect {

    private final PrometheusExceptionMonitor monitor;

    public ExceptionMonitoringAspect(PrometheusExceptionMonitor monitor) {
        this.monitor = monitor;
    }

    /**
     *  在execution(* com.atomicjar.todos.config..*(..))底下所有的method 被一個
     * try 包起來?    假設目前要執行得的是config裡下一個method我們叫做B
     * 這個  他就會是  joinPoint.proceed();
     * 執行完如果有錯 ，就會跑catch (Exception ex) {
     *         monitor.incrementExceptionCount();
     *         throw ex; // 例外計數後，將例外繼續往外拋出
     *     }並且因為有再把 ex丟回去，  所以原本的B 也可以拿到ex資訊
     */

    @Around("execution(* com.atomicjar.todos.config..*(..)) || execution(* com.atomicjar.todos.repository..*(..))")
    public Object monitorExceptions(ProceedingJoinPoint joinPoint) throws Throwable {
        try {
            return joinPoint.proceed();
        } catch (Exception ex) {
            monitor.incrementExceptionCount();
            // 如果不想往外拋異常，可以決定是否拋出或返回特定結果
            throw ex; // 可改成不拋出，但不建議吞異常破壞流程
        }
    }

    /*
    @Around註解表示這是一個「環繞通知（Around advice）」，會包裹匹配切入點（pointcut）的目標方法執行過程。
    切入點語法 "execution(* com.atomicjar.todos.config..*(..)) || execution(* com.atomicjar.todos.repository..*(..))"
    意味著攔截com.atomicjar.todos.config 和 com.atomicjar.todos.repository 兩個包內的所有方法執行。
    ProceedingJoinPoint 是目標方法的封裝，呼叫 joinPoint.proceed() 執行目標業務方法。
    方法中 try 先嘗試執行目標方法，若過程中拋出 Exception，會被 catch 捕獲。
    捕獲到Exception後，呼叫 monitor.incrementExceptionCount() 對Prometheus的例外計數器遞增一次，表示該異常被監控到。
    最後再將該異常透過 throw ex 繼續往外拋，確保不改變原本錯誤流程（也可根據需求改為不拋出，但通常不建議吞掉異常）。
    簡單來說，這是利用AOP環繞通知，將異常處理邏輯（計數監控）與業務邏輯分離，實現統一異常監控，且不影響原本異常流程的做法。
    只要目標方法有異常發生，就能在此統一捕獲並做指標統計。
    這個技術使得錯誤監控不必散落在各個服務邏輯內，而是集中在一處方便統計和維護.
    這段程式碼用Spring AOP的 @Around 環繞通知包裹指定套件（config 和 repository）的所有方法執行。
    當目標方法執行時，呼叫 joinPoint.proceed() 執行業務邏輯。​
    若該方法執行過程中拋出例外，會被 catch 到，進而呼叫 monitor.incrementExceptionCount() 增加Prometheus計數器，
    代表捕捉到一條異常事件。最後用 throw ex 將異常繼續拋出，保證原始流程不被中斷。
    簡單說，就是用環繞通知把例外監控邏輯從業務代碼抽離出來，實現全域異常統計，不須在每個方法重複寫try-catch，
    也不改變異常傳遞行為，達到統一監控目的.
     */

}
