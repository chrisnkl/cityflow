package com.chrisnkl.cfbackend.application.result.report;

import java.util.List;
import java.util.Map;

public record UserStatsResult(
        long totalSubmitted,
        long totalValidated,
        long totalNotifications,
        List<CategoryStat> topCategories,
        Map<String, Long> statusBreakdown
) {
    public record CategoryStat(
            String categoryName,
            long count,
            double percentage
    ) {
    }
}
