package com.chrisnkl.cfbackend.application.port.out.report;

import com.chrisnkl.cfbackend.domain.model.report.Report;

import java.util.UUID;

public interface CreateReportPort {

    UUID createReport(Report report);
}
