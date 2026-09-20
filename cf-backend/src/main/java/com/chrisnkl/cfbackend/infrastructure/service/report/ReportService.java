package com.chrisnkl.cfbackend.infrastructure.service.report;

import com.chrisnkl.cfbackend.application.command.report.CreateReportCommand;
import com.chrisnkl.cfbackend.application.port.in.report.CreateReportUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class ReportService implements CreateReportUseCase {


    @Override
    public void createReport(CreateReportCommand command) {

    }

}
