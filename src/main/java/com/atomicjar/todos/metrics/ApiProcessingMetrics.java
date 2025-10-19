package com.atomicjar.todos.metrics;

import io.micrometer.core.instrument.*;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;

@Component
public class ApiProcessingMetrics {

    private final Timer apiTimer;
    private final Counter successCounter;
    //private final Gauge lastUpdateGauge;
    private final AtomicReference<Double> lastUpdateEpoch = new AtomicReference<>((double) 0);
    private final AtomicReference<String> lastDataInfo = new AtomicReference<>("");
    private final AtomicReference<Integer> lastTodoId = new AtomicReference<>(0);
    private final DistributionSummary lastUpdateEpochSummary;
    private final DistributionSummary lastUpdateInfoLengthSummary;
    private final DistributionSummary lastUpdateStringHashSummary;


    public ApiProcessingMetrics(MeterRegistry registry) {
//        this.apiTimer = Timer.builder("api.db.processing.duration")
//                .description("Duration from API entry to successful DB insert")
//                .register(registry);
        this.apiTimer = Timer.builder("api.db.processing.duration")
                .description("Duration from API entry to successful DB insert")
                .publishPercentileHistogram()      // 啟用 histogram 輸出bucket
                .minimumExpectedValue(Duration.ofMillis(1))  // 設定桶下限
                .maximumExpectedValue(Duration.ofSeconds(30)) // 設定桶上限
                .register(registry);

        this.successCounter = Counter.builder("api.db.success.total")
                .description("Total successful DB inserts")
                .register(registry);

        Gauge.builder("api.db.last_update_epoch_seconds", lastUpdateEpoch, AtomicReference::get)
                .description("Epoch timestamp of last DB update")
                .register(registry);

        lastUpdateEpochSummary = DistributionSummary.builder("api.db.last_update_epoch_seconds_summary")
                .description("Summary of recent Epoch timestamps of DB updates")
                .register(registry);


        Gauge.builder("api.db.last_update_info_length", lastDataInfo, v -> (double) v.get().length())
                .description("Length of last updated record info")
                .register(registry);
        lastUpdateInfoLengthSummary = DistributionSummary.builder("api.db.last_update_info_length_summary")
                .description("Summary of recent info length")
                .register(registry);

        Gauge.builder("api.db.last_update_string_hash", lastTodoId, v -> v.get() == null ? 0 : v.get().hashCode())
                .description("Hash of last updated Todo.toString()")
                .register(registry);
        lastUpdateStringHashSummary = DistributionSummary.builder("api.db.last_update_string_hash_summary")
                .description("Summary of recent Todo.toString() hash")
                .register(registry);

    }

    public <T> T recordApiToDb(Supplier<T> action, String recordInfo) {
        return apiTimer.record(() -> {
            T result = action.get();
            successCounter.increment();
            lastUpdateEpoch.set((double) Instant.now().getEpochSecond());
            lastDataInfo.set(recordInfo);
            return result;
        });
    }
    public void recordLastTodoId(int id) {
        lastTodoId.set(id);
    }
    // 記錄每次時間點（秒）
    public void recordUpdateEpoch(double epochSeconds) {
        lastUpdateEpochSummary.record(epochSeconds);
    }
    // 記錄每次長度
    public void recordUpdateInfoLength(double length) {
        lastUpdateInfoLengthSummary.record(length);
    }
    public void recordStringHash(int hash) {
        lastUpdateStringHashSummary.record(hash);
    }
}
