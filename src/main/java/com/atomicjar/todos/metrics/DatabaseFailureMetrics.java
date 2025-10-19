package com.atomicjar.todos.metrics;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.stereotype.Component;

@Component
public class DatabaseFailureMetrics {

    private final Counter dbFailureCounter;

    public DatabaseFailureMetrics(MeterRegistry meterRegistry) {
        this.dbFailureCounter = Counter.builder("database_failure_total")
                .description("Total number of database failures")
                .register(meterRegistry);

    }

    public void incrementFailure() {
        dbFailureCounter.increment();
    }
}
