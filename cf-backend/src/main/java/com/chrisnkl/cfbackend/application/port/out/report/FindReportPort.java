package com.chrisnkl.cfbackend.application.port.out.report;

import com.chrisnkl.cfbackend.domain.model.report.Report;

import java.util.Optional;
import java.util.UUID;

public interface FindReportPort {

    Optional<Report> findById(UUID reportId);
}
