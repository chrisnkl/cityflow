package com.chrisnkl.cfbackend.infrastructure.dto.request.report;

import com.chrisnkl.cfbackend.domain.enums.ValidationAction;
import com.chrisnkl.cfbackend.domain.model.report.ReportStatus;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record ReviewReportRequest(
        @NotNull UUID reportId,
        @NotNull ValidationAction validationAction,
        ReportStatus status
) { }
