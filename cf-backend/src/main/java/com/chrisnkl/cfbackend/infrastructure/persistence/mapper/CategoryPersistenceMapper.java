package com.chrisnkl.cfbackend.infrastructure.persistence.mapper;

import com.chrisnkl.cfbackend.domain.model.report.Category;
import com.chrisnkl.cfbackend.infrastructure.persistence.entity.report.CategoryEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CategoryPersistenceMapper {

    CategoryEntity toEntity(Category category);

    Category toDomain(CategoryEntity entity);

}
