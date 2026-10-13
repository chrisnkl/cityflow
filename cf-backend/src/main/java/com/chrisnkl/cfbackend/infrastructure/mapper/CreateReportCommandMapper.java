package com.chrisnkl.cfbackend.infrastructure.mapper;

import com.chrisnkl.cfbackend.application.command.report.CreateReportCommand;
import com.chrisnkl.cfbackend.infrastructure.dto.request.report.CreateReportRequest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CreateReportCommandMapper {

    @Mapping(target = "createdBy", source = "createdBy")
    CreateReportCommand toCommand(CreateReportRequest request, String createdBy);
}
