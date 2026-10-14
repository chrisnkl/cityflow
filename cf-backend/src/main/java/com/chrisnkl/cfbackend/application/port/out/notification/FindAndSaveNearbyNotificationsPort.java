package com.chrisnkl.cfbackend.application.port.out.notification;

import com.chrisnkl.cfbackend.application.result.notification.ReportNotificationResult;

import java.util.List;

public interface FindAndSaveNearbyNotificationsPort {

    List<ReportNotificationResult> findAndSaveNewForUser(String email, double latitude, double longitude);
}
