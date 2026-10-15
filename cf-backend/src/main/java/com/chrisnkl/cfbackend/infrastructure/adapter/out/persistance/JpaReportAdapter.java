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
import com.chrisnkl.cfbackend.infrastructure.persistence.repository.JpaUserNotificationRepository;
import com.chrisnkl.cfbackend.infrastructure.persistence.repository.JpaUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.UUID;

import com.chrisnkl.cfbackend.application.command.report.GetUserStatsCommand;
import com.chrisnkl.cfbackend.application.result.report.UserStatsResult;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;

@Repository
@RequiredArgsConstructor
public class JpaReportAdapter implements FindNearbyReportsPort, CreateReportPort, GetCategoriesPort, FindReportPort,
        ManageReportPort, GetUserStatsPort {

    private final JpaReportRepository reportRepository;
    private final JpaCategoryRepository categoryRepository;
    private final JpaUserRepository userRepository;
    private final JpaNotificationRepository notificationRepository;
    private final JpaUserNotificationRepository userNotificationRepository;

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

    @Override
    @Transactional(readOnly = true)
    public UserStatsResult getUserStats(GetUserStatsCommand command) {
        String email = command.userEmail();

        Specification<ReportEntity> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            // User filter: reports submitted by the user
            predicates.add(cb.equal(cb.lower(root.get("createdBy").get("email")), email.toLowerCase()));

            // Time filter
            if (command.timeFilter() != null && !command.timeFilter().isBlank()) {
                Instant cutoff = calculateCutoff(command.timeFilter().trim().toLowerCase());
                if (cutoff != null) {
                    predicates.add(cb.greaterThanOrEqualTo(root.get("createdAt"), cutoff));
                }
            }

            // Category filter
            if (command.categoryId() != null) {
                predicates.add(cb.equal(root.get("category").get("id"), command.categoryId()));
            }

            // Status filter
            if (command.status() != null && !command.status().isBlank() && !"ALL".equalsIgnoreCase(command.status())) {
                try {
                    ReportStatus statusEnum = ReportStatus.valueOf(command.status().trim().toUpperCase());
                    predicates.add(cb.equal(root.get("status"), statusEnum));
                } catch (IllegalArgumentException ignored) {
                    // If unknown status passed, query yields empty or matches no status
                    predicates.add(cb.disjunction());
                }
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };

        List<ReportEntity> reports = reportRepository.findAll(spec);

        long totalSubmitted = reports.size();

        // Validated means reports that municipal employees acted upon (UNDER_INVESTIGATION, IN_PROGRESS, RESOLVED)
        long totalValidated = reports.stream()
                .filter(r -> r.getStatus() == ReportStatus.UNDER_INVESTIGATION
                        || r.getStatus() == ReportStatus.IN_PROGRESS
                        || r.getStatus() == ReportStatus.RESOLVED)
                .count();

        // Total notifications received by user
        long totalNotifications = userNotificationRepository.countByUser_Email(email);

        // Status breakdown initialized with all standard statuses
        Map<String, Long> statusBreakdown = new LinkedHashMap<>();
        for (ReportStatus status : ReportStatus.values()) {
            statusBreakdown.put(status.name(), 0L);
        }
        for (ReportEntity report : reports) {
            if (report.getStatus() != null) {
                statusBreakdown.computeIfPresent(report.getStatus().name(), (k, v) -> v + 1);
            }
        }

        // Top categories
        Map<String, Long> categoryCountMap = new HashMap<>();
        for (ReportEntity report : reports) {
            String catName = report.getCategory() != null ? report.getCategory().getName() : "Other";
            categoryCountMap.put(catName, categoryCountMap.getOrDefault(catName, 0L) + 1L);
        }

        List<UserStatsResult.CategoryStat> topCategories = categoryCountMap.entrySet().stream()
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                .limit(5)
                .map(entry -> {
                    double percentage = totalSubmitted > 0
                            ? Math.round((entry.getValue() * 100.0 / totalSubmitted) * 10.0) / 10.0
                            : 0.0;
                    return new UserStatsResult.CategoryStat(entry.getKey(), entry.getValue(), percentage);
                })
                .toList();

        return new UserStatsResult(
                totalSubmitted,
                totalValidated,
                totalNotifications,
                topCategories,
                statusBreakdown
        );
    }

    private Instant calculateCutoff(String filter) {
        Instant now = Instant.now();
        return switch (filter) {
            case "today" -> now.minus(1, ChronoUnit.DAYS);
            case "week" -> now.minus(7, ChronoUnit.DAYS);
            case "month" -> now.minus(30, ChronoUnit.DAYS);
            case "year" -> now.minus(365, ChronoUnit.DAYS);
            default -> null; // "all" or unspecified
        };
    }
}
