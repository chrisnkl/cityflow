package com.chrisnkl.cfbackend.application.port.out.notification;

import com.chrisnkl.cfbackend.application.result.notification.ReportNotificationResult;
import com.chrisnkl.cfbackend.application.result.pagination.PageResult;

import java.util.List;
import java.util.UUID;

public interface ManageUserNotificationsPort {

    PageResult<ReportNotificationResult> findForUser(String email, int page, int size, boolean unreadOnly);

    int markReadForUser(String email, List<UUID> notificationIds);

    int markAllReadForUser(String email);
}
