package com.chrisnkl.cfbackend.infrastructure.controller;

import com.chrisnkl.cfbackend.application.port.in.notification.ManageNotificationsUseCase;
import com.chrisnkl.cfbackend.application.result.notification.ReportNotificationResult;
import com.chrisnkl.cfbackend.application.result.pagination.PageResult;
import com.chrisnkl.cfbackend.infrastructure.common.ApiResponse;
import com.chrisnkl.cfbackend.infrastructure.dto.request.notification.MarkNotificationsReadRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/{v}/secure/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final ManageNotificationsUseCase manageNotificationsUseCase;

    @GetMapping
    public ResponseEntity<ApiResponse<PageResult<ReportNotificationResult>>> getNotifications(
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "50") @Min(1) @Max(100) int size,
            @RequestParam(defaultValue = "false") boolean unreadOnly,
            Authentication authentication
    ) {
        return ResponseEntity.ok(ApiResponse.success(
                "Notifications retrieved successfully.",
                manageNotificationsUseCase.findNotifications(authentication.getName(), page, size, unreadOnly)
        ));
    }

    @PatchMapping("/read")
    public ResponseEntity<ApiResponse<Integer>> markNotificationsRead(
            @Valid @RequestBody MarkNotificationsReadRequest request,
            Authentication authentication
    ) {
        return ResponseEntity.ok(ApiResponse.success(
                "Notifications marked as read.",
                manageNotificationsUseCase.markNotificationsRead(authentication.getName(), request.notificationIds())
        ));
    }

    @PatchMapping("/read-all")
    public ResponseEntity<ApiResponse<Integer>> markAllNotificationsRead(Authentication authentication) {
        return ResponseEntity.ok(ApiResponse.success(
                "All notifications marked as read.",
                manageNotificationsUseCase.markAllNotificationsRead(authentication.getName())
        ));
    }
}
