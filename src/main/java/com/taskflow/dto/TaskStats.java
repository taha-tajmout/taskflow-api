package com.taskflow.dto;

import java.util.Map;

public record TaskStats(
        long total,
        Map<String, Long> byStatus,
        Map<String, Long> byPriority,
        long overdue,
        double completionRate
) {
}