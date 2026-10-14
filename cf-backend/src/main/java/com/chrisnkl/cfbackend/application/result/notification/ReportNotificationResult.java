package com.chrisnkl.cfbackend.application.result.notification;

import java.time.Instant;
import java.util.UUID;

public record ReportNotificationResult(
        String type,
        UUID id,
        UUID reportId,
        String category,
        double latitude,
        double longitude,
        String msg,
        Instant createdAt,
        boolean read
) {
}
