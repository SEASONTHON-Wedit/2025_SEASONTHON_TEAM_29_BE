package com.wedit.backend.common.util;

import lombok.extern.slf4j.Slf4j;

/**
 * 성능 측정을 위한 유틸리티 클래스
 */
@Slf4j
public class PerformanceTimer {
    
    private final String operationName;
    private final long startTime;
    
    private PerformanceTimer(String operationName) {
        this.operationName = operationName;
        this.startTime = System.currentTimeMillis();
        log.info("[PERFORMANCE] {} 시작", operationName);
    }
    
    public static PerformanceTimer start(String operationName) {
        return new PerformanceTimer(operationName);
    }
    
    public long stop() {
        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;
        log.info("[PERFORMANCE] {} 완료 - 소요시간: {}ms", operationName, duration);
        return duration;
    }
    
    public long stopAndGet() {
        return stop();
    }
}
