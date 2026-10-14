package com.chrisnkl.cfbackend.application.port.in.notification;

import com.chrisnkl.cfbackend.application.result.notification.ReportNotificationResult;

import java.util.List;

public interface LocationNotificationUseCase {

    List<ReportNotificationResult> findNewNearbyNotifications(String email, double latitude, double longitude);
}
