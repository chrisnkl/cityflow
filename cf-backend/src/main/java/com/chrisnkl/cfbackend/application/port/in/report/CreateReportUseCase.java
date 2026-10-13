package com.chrisnkl.cfbackend.application.port.in.report;

import com.chrisnkl.cfbackend.application.command.report.CreateReportCommand;
import com.chrisnkl.cfbackend.application.result.report.CreateReportResult;

public interface CreateReportUseCase {

    CreateReportResult createReport(CreateReportCommand command);
}
