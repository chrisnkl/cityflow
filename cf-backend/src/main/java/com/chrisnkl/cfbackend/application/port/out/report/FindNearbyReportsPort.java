package com.chrisnkl.cfbackend.application.port.out.report;

import com.chrisnkl.cfbackend.domain.model.report.Report;
import com.chrisnkl.cfbackend.application.pagination.PageRequest;
import com.chrisnkl.cfbackend.application.pagination.PageResult;

public interface FindNearbyReportsPort {

    PageResult<Report> findNearbyReports(String email, double latitude, double longitude, PageRequest pageRequest);
}
