package com.chrisnkl.cfbackend.application.service.report;

import com.chrisnkl.cfbackend.application.command.report.CreateCategoryCommand;
import com.chrisnkl.cfbackend.application.command.report.UpdateCategoryCommand;
import com.chrisnkl.cfbackend.application.port.in.report.CreateCategoryUseCase;
import com.chrisnkl.cfbackend.application.port.in.report.DeleteCategoryUseCase;
import com.chrisnkl.cfbackend.application.port.in.report.GetCategoriesUseCase;
import com.chrisnkl.cfbackend.application.port.in.report.UpdateCategoryUseCase;
import com.chrisnkl.cfbackend.application.port.out.report.GetCategoriesPort;
import com.chrisnkl.cfbackend.application.port.out.report.ManageCategoryPort;
import com.chrisnkl.cfbackend.application.result.report.GetCategoriesResult;
import com.chrisnkl.cfbackend.domain.exception.HttpBackendException;
import com.chrisnkl.cfbackend.domain.model.report.Category;
import org.springframework.http.HttpStatus;

import java.util.UUID;

public class CategoryService implements GetCategoriesUseCase, CreateCategoryUseCase, UpdateCategoryUseCase, DeleteCategoryUseCase {

    private final GetCategoriesPort getCategoriesPort;
    private final ManageCategoryPort manageCategoryPort;

    public CategoryService(GetCategoriesPort getCategoriesPort, ManageCategoryPort manageCategoryPort) {
        this.getCategoriesPort = getCategoriesPort;
        this.manageCategoryPort = manageCategoryPort;
    }

    @Override
    public GetCategoriesResult getCategories(boolean enabled, double radius) {
        return getCategoriesPort.getCategories(enabled, radius);
    }

    @Override
    public UUID createCategory(CreateCategoryCommand command) {
        if (command == null) {
            throw new HttpBackendException(HttpStatus.BAD_REQUEST.value(), "Category details are required.");
        }
        if (command.name() == null || command.name().trim().isEmpty()) {
            throw new HttpBackendException(HttpStatus.BAD_REQUEST.value(), "Category name is required.");
        }
        if (command.radius() <= 0) {
            throw new HttpBackendException(HttpStatus.BAD_REQUEST.value(), "Radius must be greater than 0.");
        }
        if (manageCategoryPort.existsByName(command.name().trim())) {
            throw new HttpBackendException(HttpStatus.CONFLICT.value(), "Category with name '" + command.name().trim() + "' already exists.");
        }

        Category category = Category.builder()
                .name(command.name().trim())
                .description(command.description())
                .tip(command.tip())
                .enabled(command.enabled())
                .radius(command.radius())
                .build();

        return manageCategoryPort.createCategory(category);
    }

    @Override
    public Category updateCategory(UpdateCategoryCommand command) {
        if (command == null || command.id() == null) {
            throw new HttpBackendException(HttpStatus.BAD_REQUEST.value(), "Category ID is required for update.");
        }
        if (command.name() == null || command.name().trim().isEmpty()) {
            throw new HttpBackendException(HttpStatus.BAD_REQUEST.value(), "Category name is required.");
        }
        if (command.radius() <= 0) {
            throw new HttpBackendException(HttpStatus.BAD_REQUEST.value(), "Radius must be greater than 0.");
        }

        Category existingCategory = manageCategoryPort.findById(command.id())
                .orElseThrow(() -> new HttpBackendException(HttpStatus.NOT_FOUND.value(), "Category not found."));

        if (manageCategoryPort.existsByNameAndIdNot(command.name().trim(), command.id())) {
            throw new HttpBackendException(HttpStatus.CONFLICT.value(), "Category with name '" + command.name().trim() + "' already exists.");
        }

        existingCategory.setName(command.name().trim());
        existingCategory.setDescription(command.description());
        existingCategory.setTip(command.tip());
        existingCategory.setEnabled(command.enabled());
        existingCategory.setRadius(command.radius());

        return manageCategoryPort.updateCategory(existingCategory);
    }

    @Override
    public void deleteCategory(UUID id) {
        if (id == null) {
            throw new HttpBackendException(HttpStatus.BAD_REQUEST.value(), "Category ID is required.");
        }

        if (!manageCategoryPort.findById(id).isPresent()) {
            throw new HttpBackendException(HttpStatus.NOT_FOUND.value(), "Category not found.");
        }

        if (manageCategoryPort.isCategoryInUse(id)) {
            throw new HttpBackendException(HttpStatus.CONFLICT.value(), "Cannot delete category because it is associated with existing reports.");
        }

        manageCategoryPort.deleteCategory(id);
    }
}
