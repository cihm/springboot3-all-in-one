package com.atomicjar.todos.metrics;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.stereotype.Component;

@Component
public class PrometheusExceptionMonitor {

    private final Counter exceptionCounter;

    public PrometheusExceptionMonitor(MeterRegistry meterRegistry) {
        this.exceptionCounter = Counter.builder("application_exceptions_total")
                .description("Total exceptions thrown")
                .register(meterRegistry);
    }

    public void incrementExceptionCount() {
        exceptionCounter.increment();
    }
}
