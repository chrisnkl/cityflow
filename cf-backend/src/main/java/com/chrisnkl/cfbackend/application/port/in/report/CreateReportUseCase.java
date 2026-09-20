package com.chrisnkl.cfbackend.application.port.in.report;

import com.chrisnkl.cfbackend.application.command.report.CreateReportCommand;

public interface CreateReportUseCase {

    void createReport(CreateReportCommand command);
}
