package com.chrisnkl.cfbackend.infrastructure.mapper;

import com.chrisnkl.cfbackend.application.command.report.CreateCategoryCommand;
import com.chrisnkl.cfbackend.application.command.report.UpdateCategoryCommand;
import com.chrisnkl.cfbackend.infrastructure.dto.request.report.CreateCategoryRequest;
import com.chrisnkl.cfbackend.infrastructure.dto.request.report.UpdateCategoryRequest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.UUID;

@Mapper(componentModel = "spring")
public interface CategoryCommandMapper {

    CreateCategoryCommand toCommand(CreateCategoryRequest request);

    @Mapping(target = "id", source = "id")
    UpdateCategoryCommand toCommand(UUID id, UpdateCategoryRequest request);

}
