package com.chrisnkl.cfbackend.application.command.report;

import com.chrisnkl.cfbackend.domain.enums.ValidationAction;
import com.chrisnkl.cfbackend.domain.model.report.ReportStatus;

import java.util.UUID;

public record ReviewReportCommand(
        UUID reportId,
        ValidationAction validationAction,
        ReportStatus status
) { }
