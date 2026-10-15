package com.chrisnkl.cfbackend.infrastructure.controller;

import com.chrisnkl.cfbackend.application.command.report.CreateCategoryCommand;
import com.chrisnkl.cfbackend.application.command.report.UpdateCategoryCommand;
import com.chrisnkl.cfbackend.application.port.in.report.CreateCategoryUseCase;
import com.chrisnkl.cfbackend.application.port.in.report.DeleteCategoryUseCase;
import com.chrisnkl.cfbackend.application.port.in.report.GetCategoriesUseCase;
import com.chrisnkl.cfbackend.application.port.in.report.UpdateCategoryUseCase;
import com.chrisnkl.cfbackend.application.result.report.GetCategoriesResult;
import com.chrisnkl.cfbackend.domain.model.report.Category;
import com.chrisnkl.cfbackend.infrastructure.common.ApiResponse;
import com.chrisnkl.cfbackend.infrastructure.dto.request.report.CreateCategoryRequest;
import com.chrisnkl.cfbackend.infrastructure.dto.request.report.UpdateCategoryRequest;
import com.chrisnkl.cfbackend.infrastructure.dto.response.report.CategoryResponse;
import com.chrisnkl.cfbackend.infrastructure.mapper.CategoryCommandMapper;
import com.chrisnkl.cfbackend.infrastructure.mapper.CategoryResponseMapper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("api/{v}/secure/category")
@RequiredArgsConstructor
@Slf4j
public class CategoryController {

    private final GetCategoriesUseCase getCategoriesUseCase;
    private final CreateCategoryUseCase createCategoryUseCase;
    private final UpdateCategoryUseCase updateCategoryUseCase;
    private final DeleteCategoryUseCase deleteCategoryUseCase;
    private final CategoryResponseMapper categoryResponseMapper;
    private final CategoryCommandMapper categoryCommandMapper;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN') and hasAuthority('MANAGE_CATEGORIES')")
    public ResponseEntity<ApiResponse<UUID>> createCategory(
            @Valid @RequestBody CreateCategoryRequest request
    ) {
        log.info("Creating category: {}", request.name());
        CreateCategoryCommand command = categoryCommandMapper.toCommand(request);
        UUID categoryId = createCategoryUseCase.createCategory(command);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Category created successfully.", categoryId));
    }

    @GetMapping
    @PreAuthorize("hasRole('USER') or hasAuthority('REPORT_ISSUE')")
    public ResponseEntity<ApiResponse<List<CategoryResponse>>> getCategories(
            @RequestParam(defaultValue = "true", required = false) boolean enabled,
            @RequestParam(defaultValue = "100.0", required = false) double radius
    ) {
        GetCategoriesResult results = getCategoriesUseCase.getCategories(enabled, radius);
        List<CategoryResponse> categories = categoryResponseMapper.toCategoryResponse(results);

        return ResponseEntity.ok(ApiResponse.success("Retrieved categories successfully.", categories));
    }

    @PutMapping("{id}")
    @PreAuthorize("hasRole('ADMIN') and hasAuthority('MANAGE_CATEGORIES')")
    public ResponseEntity<ApiResponse<CategoryResponse>> updateCategory(
            @PathVariable(name = "id") UUID id,
            @Valid @RequestBody UpdateCategoryRequest request
    ) {
        log.info("Updating category with id: {}", id);
        UpdateCategoryCommand command = categoryCommandMapper.toCommand(id, request);
        Category updatedCategory = updateCategoryUseCase.updateCategory(command);
        CategoryResponse response = categoryResponseMapper.toCategoryResponse(updatedCategory);

        return ResponseEntity.ok(ApiResponse.success("Category updated successfully.", response));
    }

    @DeleteMapping("{id}")
    @PreAuthorize("hasRole('ADMIN') and hasAuthority('MANAGE_CATEGORIES')")
    public ResponseEntity<ApiResponse<String>> deleteCategory(@PathVariable(name = "id") UUID id) {
        log.info("Deleting category with id: {}", id);
        deleteCategoryUseCase.deleteCategory(id);

        return ResponseEntity.ok(ApiResponse.success("Category deleted successfully.", id.toString()));
    }

}
