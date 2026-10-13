package com.chrisnkl.cfbackend.infrastructure.persistence.mapper;

import com.chrisnkl.cfbackend.domain.model.report.Category;
import com.chrisnkl.cfbackend.infrastructure.dto.response.report.CategoryResponse;
import com.chrisnkl.cfbackend.infrastructure.persistence.entity.report.CategoryEntity;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CategoryPersistenceMapper {

    CategoryEntity toEntity(Category category);

    Category toDomain(CategoryEntity entity);

    List<Category> toDomain(List<CategoryEntity> categories);

}
