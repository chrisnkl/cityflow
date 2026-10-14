package com.chrisnkl.cfbackend.application.port.in.report;

import com.chrisnkl.cfbackend.application.result.report.ReportImageResult;

import java.util.UUID;

public interface GetReportImageUseCase {

    ReportImageResult getReportImage(UUID reportId, String requesterEmail, boolean canViewAllReports);
}
