package com.chrisnkl.cfbackend.infrastructure.controller;

import com.chrisnkl.cfbackend.application.port.in.report.GetCategoriesUseCase;
import com.chrisnkl.cfbackend.application.result.report.GetCategoriesResult;
import com.chrisnkl.cfbackend.infrastructure.common.ApiResponse;
import com.chrisnkl.cfbackend.infrastructure.dto.response.report.CategoryResponse;
import com.chrisnkl.cfbackend.infrastructure.mapper.CategoryResponseMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("api/{v}/secure/category")
@RequiredArgsConstructor
@Slf4j
public class CategoryController {

    private final GetCategoriesUseCase getCategoriesUseCase;
    private final CategoryResponseMapper categoryResponseMapper;

    @GetMapping
    public ResponseEntity<ApiResponse<List<CategoryResponse>>> getCategories(
            @RequestParam(defaultValue = "true", required = false) boolean enabled,
            @RequestParam(defaultValue = "100.0", required = false) double radius
    ) {

        GetCategoriesResult results = getCategoriesUseCase.getCategories(enabled, radius);

        List<CategoryResponse> categories = categoryResponseMapper.toCategoryResponse(results);

        return ResponseEntity.ok(ApiResponse.success("Retrieved categories successfully.", categories));

    }


}
