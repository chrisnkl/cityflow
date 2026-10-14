package com.chrisnkl.cfbackend.infrastructure.adapter.out.persistance;

import com.chrisnkl.cfbackend.application.port.out.report.*;
import com.chrisnkl.cfbackend.application.command.report.PageCommand;
import com.chrisnkl.cfbackend.application.result.pagination.PageResult;
import com.chrisnkl.cfbackend.application.result.report.GetCategoriesResult;
import com.chrisnkl.cfbackend.domain.enums.EventType;
import com.chrisnkl.cfbackend.domain.event.ReportCreatedEvent;
import com.chrisnkl.cfbackend.domain.exception.HttpBackendException;
import com.chrisnkl.cfbackend.domain.model.report.Category;
import com.chrisnkl.cfbackend.domain.model.report.Report;
import com.chrisnkl.cfbackend.infrastructure.persistence.entity.auth.UserEntity;
import com.chrisnkl.cfbackend.infrastructure.persistence.entity.event.OutboxEventEntity;
import com.chrisnkl.cfbackend.infrastructure.persistence.entity.report.CategoryEntity;
import com.chrisnkl.cfbackend.infrastructure.persistence.entity.report.ReportEntity;
import com.chrisnkl.cfbackend.infrastructure.persistence.mapper.CategoryPersistenceMapper;
import com.chrisnkl.cfbackend.infrastructure.persistence.mapper.ReportPersistenceMapper;
import com.chrisnkl.cfbackend.infrastructure.persistence.repository.JpaCategoryRepository;
import com.chrisnkl.cfbackend.infrastructure.persistence.repository.JpaOutboxEventRepository;
import com.chrisnkl.cfbackend.infrastructure.persistence.repository.JpaReportRepository;
import com.chrisnkl.cfbackend.infrastructure.persistence.repository.JpaUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class JpaReportAdapter implements FindNearbyReportsPort, CreateReportPort, GetCategoriesPort, FindReportPort {

    private final JpaReportRepository reportRepository;
    private final JpaCategoryRepository categoryRepository;
    private final JpaUserRepository userRepository;

    private final ReportCreatedEventPort reportCreatedEventPort;

    private final ReportPersistenceMapper reportPersistenceMapper;
    private final CategoryPersistenceMapper categoryPersistenceMapper;

    @Override
    @Transactional
    public UUID createReport(Report report) {
        CategoryEntity category = categoryRepository.findByIdAndEnabledTrue(report.getCategory().getId())
                .orElseThrow(() -> new HttpBackendException(HttpStatus.NOT_FOUND.value(), "Enabled report category not found."));
        UserEntity createdBy = userRepository.findByEmail(report.getCreatedBy().getEmail())
                .orElseThrow(() -> new HttpBackendException(HttpStatus.NOT_FOUND.value(), "Report creator not found."));

        ReportEntity entity = reportPersistenceMapper.toNewEntity(report);
        entity.setCategory(category);
        entity.setCreatedBy(createdBy);

        // Save both report and outbox event to avoid dual write problem
        ReportEntity savedReport = reportRepository.save(entity);
        reportCreatedEventPort.save(new ReportCreatedEvent(reportPersistenceMapper.toDomain(savedReport)));

        return report.getId();
    }

    @Override
    @Transactional(readOnly = true)
    public java.util.Optional<Report> findById(UUID reportId) {
        return reportRepository.findById(reportId).map(reportPersistenceMapper::toDomain);
    }

    @Override
    public PageResult<Report> findNearbyReports(String email, double latitude, double longitude, PageCommand pageCommand) {
        Page<ReportEntity> results =
                reportRepository.findNearbyReports(
                        email,
                        latitude,
                        longitude,
                        org.springframework.data.domain.PageRequest.of(pageCommand.page(), pageCommand.size())
                );
        Page<Report> reports = results.map(reportPersistenceMapper::toDomain);

        return new PageResult<>(
                reports.getContent(),
                reports.getNumber(),
                reports.getSize(),
                reports.getTotalElements(),
                reports.getTotalPages()
        );
    }

    @Override
    public GetCategoriesResult getCategories(boolean enabled, double radius) {

        List<CategoryEntity> results = categoryRepository.findByEnabledAndRadiusLessThanEqual(enabled, radius);

        List<Category> categories = categoryPersistenceMapper.toDomain(results);

        return new GetCategoriesResult(categories);
    }
}
