package com.chrisnkl.cfbackend.infrastructure.persistence.adapter.report;

import com.chrisnkl.cfbackend.application.port.out.report.FindNearbyReportsPort;
import com.chrisnkl.cfbackend.application.pagination.PageRequest;
import com.chrisnkl.cfbackend.application.pagination.PageResult;
import com.chrisnkl.cfbackend.domain.model.report.Report;
import com.chrisnkl.cfbackend.infrastructure.persistence.entity.report.ReportEntity;
import com.chrisnkl.cfbackend.infrastructure.persistence.mapper.ReportPersistenceMapper;
import com.chrisnkl.cfbackend.infrastructure.persistence.repository.JpaReportRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class JpaReportAdapter implements FindNearbyReportsPort {

    private final JpaReportRepository reportRepository;
    private final ReportPersistenceMapper reportPersistenceMapper;

    @Override
    public PageResult<Report> findNearbyReports(String email, double latitude, double longitude, PageRequest pageRequest) {
        Page<ReportEntity> results =
                reportRepository.findNearbyReports(
                        email,
                        latitude,
                        longitude,
                        org.springframework.data.domain.PageRequest.of(pageRequest.page(), pageRequest.size())
                );
        Page<Report> reports = results.map(reportPersistenceMapper::toDomain);

        return new PageResult<>(
                reports.getContent(),
                reports.getNumber(),
                reports.getSize(),
                reports.getTotalElements(),
                reports.getTotalPages()
        );
    }
}
