package com.chrisnkl.cfbackend.application.port.in.report;

import com.chrisnkl.cfbackend.application.command.report.ReviewReportCommand;

public interface ReviewReportUseCase {

    void reviewReport(ReviewReportCommand command);
}
