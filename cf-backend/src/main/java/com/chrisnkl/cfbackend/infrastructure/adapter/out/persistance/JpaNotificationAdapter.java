package com.chrisnkl.cfbackend.infrastructure.adapter.out.persistance;

import com.chrisnkl.cfbackend.application.port.out.notification.FindAndSaveNearbyNotificationsPort;
import com.chrisnkl.cfbackend.application.port.out.notification.ManageUserNotificationsPort;
import com.chrisnkl.cfbackend.application.result.notification.ReportNotificationResult;
import com.chrisnkl.cfbackend.application.result.pagination.PageResult;
import com.chrisnkl.cfbackend.domain.exception.HttpBackendException;
import com.chrisnkl.cfbackend.infrastructure.persistence.entity.auth.UserEntity;
import com.chrisnkl.cfbackend.infrastructure.persistence.entity.notification.NotificationEntity;
import com.chrisnkl.cfbackend.infrastructure.persistence.entity.notification.UserNotificationEntity;
import com.chrisnkl.cfbackend.infrastructure.persistence.entity.report.ReportEntity;
import com.chrisnkl.cfbackend.infrastructure.persistence.repository.JpaNotificationRepository;
import com.chrisnkl.cfbackend.infrastructure.persistence.repository.JpaReportRepository;
import com.chrisnkl.cfbackend.infrastructure.persistence.repository.JpaUserNotificationRepository;
import com.chrisnkl.cfbackend.infrastructure.persistence.repository.JpaUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class JpaNotificationAdapter implements FindAndSaveNearbyNotificationsPort, ManageUserNotificationsPort {

    private final JpaReportRepository reportRepository;
    private final JpaNotificationRepository notificationRepository;
    private final JpaUserNotificationRepository userNotificationRepository;
    private final JpaUserRepository userRepository;

    @Override
    @Transactional
    public List<ReportNotificationResult> findAndSaveNewForUser(String email, double latitude, double longitude) {
        UserEntity user = userRepository.findByEmailForNotificationUpdate(email)
                .orElseThrow(() -> new HttpBackendException(HttpStatus.UNAUTHORIZED.value(), "Authenticated user no longer exists."));
        List<ReportEntity> nearbyReports = reportRepository.findNearbyReportsForNotifications(email, latitude, longitude);
        List<ReportNotificationResult> newNotifications = new ArrayList<>();

        for (ReportEntity report : nearbyReports) {
            NotificationEntity notification = notificationRepository.findByReport_Id(report.getId())
                    .orElseGet(() -> {
                        ReportEntity lockedReport = reportRepository.findByIdForNotificationUpdate(report.getId())
                                .orElseThrow(() -> new HttpBackendException(HttpStatus.NOT_FOUND.value(), "Nearby report no longer exists."));
                        return notificationRepository.findByReport_Id(report.getId())
                                .orElseGet(() -> notificationRepository.save(
                                        new NotificationEntity(null, lockedReport, Set.of())
                                ));
                    });
            if (userNotificationRepository.existsByNotification_IdAndUser_Id(notification.getId(), user.getId())) {
                continue;
            }

            UserNotificationEntity savedNotification = userNotificationRepository.save(
                    new UserNotificationEntity(null, notification, user, false)
            );
            newNotifications.add(toResult(savedNotification));
        }
        return List.copyOf(newNotifications);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResult<ReportNotificationResult> findForUser(String email, int page, int size, boolean unreadOnly) {
        Page<UserNotificationEntity> notifications = userNotificationRepository.findForUser(
                email,
                unreadOnly,
                PageRequest.of(page, size)
        );
        Page<ReportNotificationResult> results = notifications.map(this::toResult);
        return new PageResult<>(
                results.getContent(),
                results.getNumber(),
                results.getSize(),
                results.getTotalElements(),
                results.getTotalPages()
        );
    }

    @Override
    @Transactional
    public int markReadForUser(String email, List<UUID> notificationIds) {
        return userNotificationRepository.markReadForUser(email, notificationIds);
    }

    @Override
    @Transactional
    public int markAllReadForUser(String email) {
        return userNotificationRepository.markAllReadForUser(email);
    }

    private ReportNotificationResult toResult(UserNotificationEntity userNotification) {
        ReportEntity report = userNotification.getNotification().getReport();
        String message = report.getComments();
        if (message == null || message.isBlank()) {
            message = "New nearby report: " + report.getCategory().getName();
        }
        return new ReportNotificationResult(
                "notification",
                userNotification.getId(),
                report.getId(),
                report.getCategory().getName(),
                report.getLatitude(),
                report.getLongitude(),
                message,
                userNotification.getCreatedAt(),
                userNotification.isRead()
        );
    }
}
