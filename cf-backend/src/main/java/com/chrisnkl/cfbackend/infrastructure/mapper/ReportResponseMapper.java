package com.chrisnkl.cfbackend.infrastructure.mapper;

import com.chrisnkl.cfbackend.application.result.pagination.PageResult;
import com.chrisnkl.cfbackend.domain.model.report.Report;
import com.chrisnkl.cfbackend.infrastructure.dto.response.report.ReportPageResponse;
import com.chrisnkl.cfbackend.infrastructure.dto.response.report.ReportResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ReportResponseMapper {

    ReportPageResponse toPageResponse(PageResult<Report> page);

    @Mapping(target = "categoryId", source = "category.id")
    @Mapping(target = "category", source = "category.name")
    @Mapping(target = "photoUrl", expression = "java(report.getPhotoUrl() == null ? null : \"/api/v1/secure/submission/\" + report.getId() + \"/photo\")")
    @Mapping(target = "createdBy", source = "createdBy.email")
    ReportResponse toResponse(Report report);
}
