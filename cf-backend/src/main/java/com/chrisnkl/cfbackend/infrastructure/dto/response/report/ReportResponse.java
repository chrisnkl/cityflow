package com.chrisnkl.cfbackend.infrastructure.dto.response.report;

import com.chrisnkl.cfbackend.domain.model.report.ReportStatus;

import java.time.Instant;
import java.util.UUID;

public record ReportResponse(
        UUID id,
        UUID categoryId,
        String category,
        ReportStatus status,
        double longitude,
        double latitude,
        String comments,
        String photoUrl,
        Double priority,
        String createdBy,
        Instant createdAt,
        Instant updatedAt
) {
}
