package com.chrisnkl.cfbackend.infrastructure.dto.request.report;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record UpdateCategoryRequest(

        @NotBlank(message = "Category name is required")
        @Size(max = 255, message = "Category name must not exceed 255 characters")
        String name,

        @NotBlank(message = "Category description is required")
        @Size(max = 255, message = "Category description must not exceed 255 characters")
        String description,

        @NotBlank(message = "Category tip is required")
        @Size(max = 255, message = "Category tip must not exceed 255 characters")
        String tip,

        @NotNull(message = "Category enabled flag is required")
        Boolean enabled,

        @DecimalMin(value = "0.0", inclusive = false, message = "Radius must be greater than 0")
        double radius

) {
}
