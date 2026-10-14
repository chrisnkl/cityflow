package com.chrisnkl.cfbackend.infrastructure.dto.request.notification;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

import java.util.List;
import java.util.UUID;

public record MarkNotificationsReadRequest(
        @NotEmpty
        @Size(max = 1000)
        List<UUID> notificationIds
) {
}
