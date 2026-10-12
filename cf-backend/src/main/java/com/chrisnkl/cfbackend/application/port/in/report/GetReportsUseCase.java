package com.chrisnkl.cfbackend.application.port.in.report;

import com.chrisnkl.cfbackend.domain.model.report.Report;
import com.chrisnkl.cfbackend.application.pagination.PageRequest;
import com.chrisnkl.cfbackend.application.pagination.PageResult;

public interface GetReportsUseCase {

    PageResult<Report> getReports(String email, double latitude, double longitude, PageRequest pageRequest);

}
