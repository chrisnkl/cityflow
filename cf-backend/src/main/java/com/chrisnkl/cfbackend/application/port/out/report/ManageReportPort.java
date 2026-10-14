package com.chrisnkl.cfbackend.application.port.out.report;

import com.chrisnkl.cfbackend.domain.model.report.ReportStatus;

import java.util.UUID;

public interface ManageReportPort {

    void updateStatus(UUID reportId, ReportStatus status);

    void deleteReport(UUID reportId);
}
