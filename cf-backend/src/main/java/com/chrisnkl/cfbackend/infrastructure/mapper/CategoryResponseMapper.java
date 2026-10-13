package com.chrisnkl.cfbackend.infrastructure.mapper;

import com.chrisnkl.cfbackend.application.result.report.GetCategoriesResult;
import com.chrisnkl.cfbackend.domain.model.report.Category;
import com.chrisnkl.cfbackend.infrastructure.dto.response.report.CategoryResponse;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CategoryResponseMapper {

    List<CategoryResponse> toCategoryResponse(List<Category> categories);

    default List<CategoryResponse> toCategoryResponse(GetCategoriesResult result) {
        return toCategoryResponse(result.categories());
    }

}
