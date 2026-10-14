package com.chrisnkl.cfbackend.infrastructure.adapter.out.persistance;

import com.chrisnkl.cfbackend.application.port.out.report.*;
import com.chrisnkl.cfbackend.application.command.report.PageCommand;
import com.chrisnkl.cfbackend.application.result.pagination.PageResult;
import com.chrisnkl.cfbackend.application.result.report.GetCategoriesResult;
import com.chrisnkl.cfbackend.domain.exception.HttpBackendException;
import com.chrisnkl.cfbackend.domain.model.report.Category;
import com.chrisnkl.cfbackend.domain.model.report.Report;
import com.chrisnkl.cfbackend.domain.model.report.ReportStatus;
import com.chrisnkl.cfbackend.infrastructure.persistence.entity.auth.UserEntity;
import com.chrisnkl.cfbackend.infrastructure.persistence.entity.notification.NotificationEntity;
import com.chrisnkl.cfbackend.infrastructure.persistence.entity.report.CategoryEntity;
import com.chrisnkl.cfbackend.infrastructure.persistence.entity.report.ReportEntity;
import com.chrisnkl.cfbackend.infrastructure.persistence.mapper.CategoryPersistenceMapper;
import com.chrisnkl.cfbackend.infrastructure.persistence.mapper.ReportPersistenceMapper;
import com.chrisnkl.cfbackend.infrastructure.persistence.repository.JpaCategoryRepository;
import com.chrisnkl.cfbackend.infrastructure.persistence.repository.JpaNotificationRepository;
import com.chrisnkl.cfbackend.infrastructure.persistence.repository.JpaReportRepository;
import com.chrisnkl.cfbackend.infrastructure.persistence.repository.JpaUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class JpaReportAdapter implements FindNearbyReportsPort, CreateReportPort, GetCategoriesPort, FindReportPort,
        ManageReportPort {

    private final JpaReportRepository reportRepository;
    private final JpaCategoryRepository categoryRepository;
    private final JpaUserRepository userRepository;
    private final JpaNotificationRepository notificationRepository;

    private final ReportPersistenceMapper reportPersistenceMapper;
    private final CategoryPersistenceMapper categoryPersistenceMapper;

    @Override
    @Transactional
    public UUID createReport(Report report) {
        CategoryEntity category = categoryRepository.findById(report.getCategory().getId())
                .filter(CategoryEntity::isEnabled)
                .orElseThrow(() -> new HttpBackendException(HttpStatus.BAD_REQUEST.value(), "Category not found"));
        UserEntity createdBy = userRepository.findByEmail(report.getCreatedBy().getEmail())
                .orElseThrow(() -> new HttpBackendException(HttpStatus.NOT_FOUND.value(), "Report creator not found."));

        ReportEntity entity = reportPersistenceMapper.toNewEntity(report);
        entity.setCategory(category);
        entity.setCreatedBy(createdBy);

        ReportEntity savedReport = reportRepository.save(entity);
        notificationRepository.save(new NotificationEntity(null, savedReport, Set.of()));
        return savedReport.getId();
    }

    @Override
    @Transactional(readOnly = true)
    public java.util.Optional<Report> findById(UUID reportId) {
        return reportRepository.findById(reportId).map(reportPersistenceMapper::toDomain);
    }

    @Override
    @Transactional
    public void updateStatus(UUID reportId, ReportStatus status) {
        ReportEntity report = reportRepository.findById(reportId)
                .orElseThrow(() -> new HttpBackendException(HttpStatus.NOT_FOUND.value(), "Report not found."));
        report.setStatus(status);
        reportRepository.save(report);
    }

    @Override
    @Transactional
    public void deleteReport(UUID reportId) {
        ReportEntity report = reportRepository.findById(reportId)
                .orElseThrow(() -> new HttpBackendException(HttpStatus.NOT_FOUND.value(), "Report not found."));
        reportRepository.delete(report);
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
