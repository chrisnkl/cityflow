package com.chrisnkl.cfbackend.application.service.notification;

import com.chrisnkl.cfbackend.application.port.in.notification.LocationNotificationUseCase;
import com.chrisnkl.cfbackend.application.port.in.notification.ManageNotificationsUseCase;
import com.chrisnkl.cfbackend.application.port.out.notification.FindAndSaveNearbyNotificationsPort;
import com.chrisnkl.cfbackend.application.port.out.notification.ManageUserNotificationsPort;
import com.chrisnkl.cfbackend.application.result.notification.ReportNotificationResult;
import com.chrisnkl.cfbackend.application.result.pagination.PageResult;
import com.chrisnkl.cfbackend.domain.exception.HttpBackendException;
import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.UUID;

public class NotificationService implements LocationNotificationUseCase, ManageNotificationsUseCase {

    private static final int MAX_PAGE_SIZE = 100;
    private static final int MAX_READ_BATCH_SIZE = 1000;

    private final FindAndSaveNearbyNotificationsPort findAndSaveNearbyNotificationsPort;
    private final ManageUserNotificationsPort manageUserNotificationsPort;

    public NotificationService(
            FindAndSaveNearbyNotificationsPort findAndSaveNearbyNotificationsPort,
            ManageUserNotificationsPort manageUserNotificationsPort
    ) {
        this.findAndSaveNearbyNotificationsPort = findAndSaveNearbyNotificationsPort;
        this.manageUserNotificationsPort = manageUserNotificationsPort;
    }

    @Override
    public List<ReportNotificationResult> findNewNearbyNotifications(String email, double latitude, double longitude) {
        validateEmail(email);
        validateCoordinates(latitude, longitude);
        return findAndSaveNearbyNotificationsPort.findAndSaveNewForUser(email, latitude, longitude);
    }

    @Override
    public PageResult<ReportNotificationResult> findNotifications(
            String email,
            int page,
            int size,
            boolean unreadOnly
    ) {
        validateEmail(email);
        if (page < 0 || size < 1 || size > MAX_PAGE_SIZE) {
            throw new HttpBackendException(HttpStatus.BAD_REQUEST.value(), "Notification page must be non-negative and size must be between 1 and 100.");
        }
        return manageUserNotificationsPort.findForUser(email, page, size, unreadOnly);
    }

    @Override
    public int markNotificationsRead(String email, List<UUID> notificationIds) {
        validateEmail(email);
        if (notificationIds == null || notificationIds.isEmpty() || notificationIds.size() > MAX_READ_BATCH_SIZE) {
            throw new HttpBackendException(HttpStatus.BAD_REQUEST.value(), "Provide between 1 and 1000 notification IDs.");
        }
        return manageUserNotificationsPort.markReadForUser(email, notificationIds);
    }

    @Override
    public int markAllNotificationsRead(String email) {
        validateEmail(email);
        return manageUserNotificationsPort.markAllReadForUser(email);
    }

    private void validateEmail(String email) {
        if (email == null || email.isBlank()) {
            throw new HttpBackendException(HttpStatus.UNAUTHORIZED.value(), "An authenticated user is required.");
        }
    }

    private void validateCoordinates(double latitude, double longitude) {
        if (!Double.isFinite(latitude) || !Double.isFinite(longitude)
                || latitude < -90 || latitude > 90
                || longitude < -180 || longitude > 180) {
            throw new HttpBackendException(HttpStatus.BAD_REQUEST.value(), "Location coordinates are invalid.");
        }
    }
}
