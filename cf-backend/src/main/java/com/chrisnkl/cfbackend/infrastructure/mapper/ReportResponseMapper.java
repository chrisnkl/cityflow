package com.chrisnkl.cfbackend.infrastructure.mapper;

import com.chrisnkl.cfbackend.application.pagination.PageResult;
import com.chrisnkl.cfbackend.domain.model.report.Category;
import com.chrisnkl.cfbackend.domain.model.report.Report;
import com.chrisnkl.cfbackend.infrastructure.dto.response.report.ReportPageResponse;
import com.chrisnkl.cfbackend.infrastructure.dto.response.report.ReportResponse;
import org.springframework.stereotype.Component;

@Component
public class ReportResponseMapper {

    public ReportPageResponse toPageResponse(PageResult<Report> page) {
        return new ReportPageResponse(
                page.content().stream().map(this::toResponse).toList(),
                page.page(),
                page.size(),
                page.totalElements(),
                page.totalPages()
        );
    }

    private ReportResponse toResponse(Report report) {
        Category category = report.getCategory();

        return new ReportResponse(
                report.getId(),
                category.getId(),
                category.getName(),
                report.getStatus(),
                report.getLongitude(),
                report.getLatitude(),
                report.getComments(),
                report.getPhotoUrl(),
                report.getPriority(),
                report.getCreatedAt(),
                report.getUpdatedAt()
        );
    }
}
