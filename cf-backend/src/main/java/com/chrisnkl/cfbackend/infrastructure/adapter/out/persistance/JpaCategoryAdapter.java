package com.chrisnkl.cfbackend.infrastructure.adapter.out.persistance;

import com.chrisnkl.cfbackend.application.port.out.report.ManageCategoryPort;
import com.chrisnkl.cfbackend.domain.model.report.Category;
import com.chrisnkl.cfbackend.infrastructure.persistence.entity.report.CategoryEntity;
import com.chrisnkl.cfbackend.infrastructure.persistence.mapper.CategoryPersistenceMapper;
import com.chrisnkl.cfbackend.infrastructure.persistence.repository.JpaCategoryRepository;
import com.chrisnkl.cfbackend.infrastructure.persistence.repository.JpaReportRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class JpaCategoryAdapter implements ManageCategoryPort {

    private final JpaCategoryRepository categoryRepository;
    private final JpaReportRepository reportRepository;
    private final CategoryPersistenceMapper categoryPersistenceMapper;

    @Override
    @Transactional
    public UUID createCategory(Category category) {
        CategoryEntity entity = categoryPersistenceMapper.toEntity(category);
        CategoryEntity saved = categoryRepository.save(entity);
        return saved.getId();
    }

    @Override
    public Optional<Category> findById(UUID id) {
        return categoryRepository.findById(id).map(categoryPersistenceMapper::toDomain);
    }

    @Override
    @Transactional
    public Category updateCategory(Category category) {
        CategoryEntity entity = categoryPersistenceMapper.toEntity(category);
        CategoryEntity saved = categoryRepository.save(entity);
        return categoryPersistenceMapper.toDomain(saved);
    }

    @Override
    @Transactional
    public void deleteCategory(UUID id) {
        categoryRepository.deleteById(id);
    }

    @Override
    public boolean existsByName(String name) {
        return categoryRepository.existsByName(name);
    }

    @Override
    public boolean existsByNameAndIdNot(String name, UUID id) {
        return categoryRepository.existsByNameAndIdNot(name, id);
    }

    @Override
    public boolean isCategoryInUse(UUID id) {
        return reportRepository.existsByCategoryId(id);
    }
}
