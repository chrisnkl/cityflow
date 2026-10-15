package com.chrisnkl.cfbackend.application.port.out.report;

import com.chrisnkl.cfbackend.domain.model.report.Category;

import java.util.Optional;
import java.util.UUID;

public interface ManageCategoryPort {

    UUID createCategory(Category category);

    Optional<Category> findById(UUID id);

    Category updateCategory(Category category);

    void deleteCategory(UUID id);

    boolean existsByName(String name);

    boolean existsByNameAndIdNot(String name, UUID id);

    boolean isCategoryInUse(UUID id);

}
