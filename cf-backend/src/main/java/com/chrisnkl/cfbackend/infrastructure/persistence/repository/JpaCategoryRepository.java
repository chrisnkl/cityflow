package com.chrisnkl.cfbackend.infrastructure.persistence.repository;

import com.chrisnkl.cfbackend.infrastructure.persistence.entity.report.CategoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface JpaCategoryRepository extends JpaRepository<CategoryEntity, UUID> {

    Optional<CategoryEntity> findByIdAndEnabledTrue(UUID id);

    List<CategoryEntity> findByEnabledAndRadiusLessThanEqual(boolean enabled, double radius);

    boolean existsByName(String name);

    boolean existsByNameAndIdNot(String name, UUID id);
}
