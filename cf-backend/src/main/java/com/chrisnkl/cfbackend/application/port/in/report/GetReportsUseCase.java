package com.chrisnkl.cfbackend.application.port.in.report;

import com.chrisnkl.cfbackend.domain.model.report.Report;
import com.chrisnkl.cfbackend.application.command.report.PageCommand;
import com.chrisnkl.cfbackend.application.result.pagination.PageResult;

public interface GetReportsUseCase {

    PageResult<Report> getReports(String email, double latitude, double longitude, PageCommand pageCommand);

}
