package com.chrisnkl.cfbackend.infrastructure.persistence.mapper;

import com.chrisnkl.cfbackend.domain.model.report.Report;
import com.chrisnkl.cfbackend.infrastructure.persistence.entity.report.ReportEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {CategoryPersistenceMapper.class, UserPersistenceMapper.class})
public interface ReportPersistenceMapper {

    @Mapping(target = "notification", ignore = true)
    ReportEntity toEntity(Report report);

    Report toDomain(ReportEntity entity);

}
