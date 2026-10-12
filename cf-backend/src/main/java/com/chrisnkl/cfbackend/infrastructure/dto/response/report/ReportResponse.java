package com.chrisnkl.cfbackend.infrastructure.dto.response.report;

import com.chrisnkl.cfbackend.domain.model.report.ReportStatus;

import java.time.Instant;
import java.util.UUID;

public record ReportResponse(
        UUID id,
        UUID categoryId,
        String categoryName,
        ReportStatus status,
        double longitude,
        double latitude,
        String comments,
        String photoUrl,
        Double priority,
        Instant createdAt,
        Instant updatedAt
) {
}
