package com.chrisnkl.cfbackend.infrastructure.messaging.websocket;

import com.chrisnkl.cfbackend.application.port.in.notification.LocationNotificationUseCase;
import com.chrisnkl.cfbackend.application.result.notification.ReportNotificationResult;
import com.chrisnkl.cfbackend.infrastructure.dto.request.notification.LocationUpdateMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import jakarta.validation.Valid;

@Controller
@RequiredArgsConstructor
@Validated
public class ReportNotificationService {

    private final LocationNotificationUseCase locationNotificationUseCase;
    private final SimpMessagingTemplate messagingTemplate;

    @MessageMapping("/location")
    public void updateLocation(@Valid @Payload LocationUpdateMessage update, Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new IllegalArgumentException("An authenticated user is required.");
        }
        if (!"subscribe".equalsIgnoreCase(update.type()) && !"loc".equalsIgnoreCase(update.type())) {
            throw new IllegalArgumentException("Location message type must be 'subscribe' or 'loc'.");
        }
        if (update.location() == null || update.location().lat() == null || update.location().lon() == null) {
            throw new IllegalArgumentException("Location latitude and longitude are required.");
        }

        for (ReportNotificationResult notification : locationNotificationUseCase.findNewNearbyNotifications(
                authentication.getName(),
                update.location().lat(),
                update.location().lon()
        )) {
            messagingTemplate.convertAndSendToUser(
                    authentication.getName(),
                    "/queue/notifications",
                    notification
            );
        }
    }
}
