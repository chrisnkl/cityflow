package com.chrisnkl.cfbackend.infrastructure.dto.response.report;

import java.util.List;
import java.util.Map;

public record UserStatsResponse(
        long totalSubmitted,
        long totalValidated,
        long totalNotifications,
        List<CategoryStatResponse> topCategories,
        Map<String, Long> statusBreakdown
) {
    public record CategoryStatResponse(
            String categoryName,
            long count,
            double percentage
    ) {
    }
}
