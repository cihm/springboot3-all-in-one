package com.atomicjar.todos.metrics;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

// Global exception handler to catch all exceptions and increment the Prometheus counter for controller
@ControllerAdvice
public class GlobalExceptionHandler {

    private final PrometheusExceptionMonitor monitor;

    public GlobalExceptionHandler(PrometheusExceptionMonitor monitor) {
        this.monitor = monitor;
    }

    @ExceptionHandler(Exception.class)
    @ResponseBody
    public ResponseEntity<String> handleAllExceptions(Exception ex) {
        monitor.incrementExceptionCount();
        return new ResponseEntity<>("Exception occurred: " + ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
}