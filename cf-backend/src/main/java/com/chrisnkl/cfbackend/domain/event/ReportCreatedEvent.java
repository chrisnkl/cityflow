package com.chrisnkl.cfbackend.domain.event;

import com.chrisnkl.cfbackend.domain.enums.EventType;
import com.chrisnkl.cfbackend.domain.model.report.Report;
import com.chrisnkl.cfbackend.domain.model.report.ReportStatus;

import java.time.Instant;
import java.util.UUID;

public record ReportCreatedEvent(

        UUID eventId,
        EventType eventType,
        UUID reportId,
        UUID categoryId,
        ReportStatus status,
        double latitude,
        double longitude,
        String createdBy,
        Instant timestamp

) {

    public ReportCreatedEvent {
        if (eventId == null) throw new IllegalArgumentException("Parameter 'eventId' must not be null.");
    }

    public ReportCreatedEvent(Report report) {
        this(
                UUID.randomUUID(),
                EventType.PUSH_NOTIFICATION,
                report.getId(),
                report.getCategory().getId(),
                report.getStatus(),
                report.getLatitude(),
                report.getLongitude(),
                report.getCreatedBy().getEmail(),
                Instant.now()
        );
    }

}
