package com.chrisnkl.cfbackend;

import com.chrisnkl.cfbackend.application.result.notification.ReportNotificationResult;
import com.chrisnkl.cfbackend.application.command.report.ReviewReportCommand;
import com.chrisnkl.cfbackend.application.port.in.report.ReviewReportUseCase;
import com.chrisnkl.cfbackend.domain.exception.HttpBackendException;
import com.chrisnkl.cfbackend.domain.enums.ValidationAction;
import com.chrisnkl.cfbackend.domain.model.report.Category;
import com.chrisnkl.cfbackend.domain.model.report.Report;
import com.chrisnkl.cfbackend.domain.model.report.ReportStatus;
import com.chrisnkl.cfbackend.domain.model.auth.User;
import com.chrisnkl.cfbackend.infrastructure.adapter.out.persistance.JpaReportAdapter;
import com.chrisnkl.cfbackend.infrastructure.adapter.out.persistance.JpaNotificationAdapter;
import com.chrisnkl.cfbackend.infrastructure.persistence.entity.auth.UserEntity;
import com.chrisnkl.cfbackend.infrastructure.persistence.entity.notification.NotificationEntity;
import com.chrisnkl.cfbackend.infrastructure.persistence.entity.notification.UserNotificationEntity;
import com.chrisnkl.cfbackend.infrastructure.persistence.entity.report.CategoryEntity;
import com.chrisnkl.cfbackend.infrastructure.persistence.entity.report.ReportEntity;
import com.chrisnkl.cfbackend.infrastructure.persistence.repository.JpaCategoryRepository;
import com.chrisnkl.cfbackend.infrastructure.persistence.repository.JpaNotificationRepository;
import com.chrisnkl.cfbackend.infrastructure.persistence.repository.JpaReportRepository;
import com.chrisnkl.cfbackend.infrastructure.persistence.repository.JpaUserNotificationRepository;
import com.chrisnkl.cfbackend.infrastructure.persistence.repository.JpaUserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class LocationNotificationIntegrationTests {

    @Autowired
    private JpaUserRepository userRepository;

    @Autowired
    private JpaCategoryRepository categoryRepository;

    @Autowired
    private JpaReportRepository reportRepository;

    @Autowired
    private JpaNotificationRepository notificationRepository;

    @Autowired
    private JpaUserNotificationRepository userNotificationRepository;

    @Autowired
    private JpaReportAdapter reportAdapter;

    @Autowired
    private ReviewReportUseCase reviewReportUseCase;

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private JpaNotificationAdapter notificationAdapter;

    @Test
    void createsUnreadNearbyNotificationOnlyOnceAndAllowsBulkRead() {
        UserEntity owner = user("owner@example.com");
        UserEntity recipient = user("recipient@example.com");
        CategoryEntity category = categoryRepository.save(category());
        ReportEntity report = reportRepository.save(report(category, owner));

        assertEquals(0, notificationAdapter.findAndSaveNewForUser(
                owner.getEmail(),
                report.getLatitude(),
                report.getLongitude()
        ).size());

        List<ReportNotificationResult> firstUpdate = notificationAdapter.findAndSaveNewForUser(
                recipient.getEmail(),
                report.getLatitude(),
                report.getLongitude()
        );
        List<ReportNotificationResult> secondUpdate = notificationAdapter.findAndSaveNewForUser(
                recipient.getEmail(),
                report.getLatitude(),
                report.getLongitude()
        );

        assertEquals(1, firstUpdate.size());
        assertEquals("notification", firstUpdate.getFirst().type());
        assertEquals(report.getId(), firstUpdate.getFirst().reportId());
        assertFalse(firstUpdate.getFirst().read());
        assertNotNull(firstUpdate.getFirst().createdAt());
        assertEquals(0, secondUpdate.size());

        assertEquals(0, notificationAdapter.markReadForUser("owner@example.com", List.of(firstUpdate.getFirst().id())));
        assertEquals(1, notificationAdapter.markReadForUser(recipient.getEmail(), List.of(firstUpdate.getFirst().id())));
        assertEquals(0, notificationAdapter.findForUser(recipient.getEmail(), 0, 50, true).totalElements());
        assertEquals(1, notificationAdapter.findForUser(recipient.getEmail(), 0, 50, false).content().size());
    }

    @Test
    void creatingReportAlsoCreatesItsNotificationRecord() {
        UserEntity owner = user("report-owner@example.com");
        CategoryEntity category = categoryRepository.save(category());
        UUID reportId = UUID.randomUUID();
        Report report = Report.builder()
                .id(reportId)
                .category(Category.builder().id(category.getId()).build())
                .status(ReportStatus.PENDING)
                .latitude(37.9838)
                .longitude(23.7275)
                .createdBy(User.builder().email(owner.getEmail()).build())
                .build();

        reportAdapter.createReport(report);

        assertEquals(reportId, notificationRepository.findByReport_Id(reportId).orElseThrow()
                .getReport().getId());
    }

    @Test
    void creatingReportWithMissingCategoryReturnsBadRequest() {
        UserEntity owner = user("missing-category-owner@example.com");
        UUID missingCategoryId = UUID.randomUUID();
        Report report = Report.builder()
                .id(UUID.randomUUID())
                .category(Category.builder().id(missingCategoryId).build())
                .status(ReportStatus.PENDING)
                .latitude(37.9838)
                .longitude(23.7275)
                .createdBy(User.builder().email(owner.getEmail()).build())
                .build();

        HttpBackendException exception = assertThrows(HttpBackendException.class,
                () -> reportAdapter.createReport(report));

        assertEquals(400, exception.getStatus());
        assertEquals("Category not found", exception.getMessage());
    }

    @Test
    void validationActionUpdatesReportStatus() {
        UserEntity owner = user("validation-owner@example.com");
        CategoryEntity category = categoryRepository.save(category());
        UUID reportId = UUID.randomUUID();
        reportAdapter.createReport(Report.builder()
                .id(reportId)
                .category(Category.builder().id(category.getId()).build())
                .status(ReportStatus.PENDING)
                .latitude(37.9838)
                .longitude(23.7275)
                .createdBy(User.builder().email(owner.getEmail()).build())
                .build());

        reviewReportUseCase.reviewReport(new ReviewReportCommand(
                reportId, ValidationAction.VALIDATE, ReportStatus.IN_PROGRESS));

        assertEquals(ReportStatus.IN_PROGRESS, reportRepository.findById(reportId).orElseThrow().getStatus());
    }

    @Test
    void rejectActionDeletesReportAndItsNotification() {
        UserEntity owner = user("rejection-owner@example.com");
        CategoryEntity category = categoryRepository.save(category());
        UUID reportId = UUID.randomUUID();
        reportAdapter.createReport(Report.builder()
                .id(reportId)
                .category(Category.builder().id(category.getId()).build())
                .status(ReportStatus.PENDING)
                .latitude(37.9838)
                .longitude(23.7275)
                .createdBy(User.builder().email(owner.getEmail()).build())
                .build());
        NotificationEntity notification = notificationRepository.findByReport_Id(reportId).orElseThrow();
        UserNotificationEntity userNotification = userNotificationRepository.save(
                new UserNotificationEntity(null, notification, owner, false));
        UUID userNotificationId = userNotification.getId();
        entityManager.flush();
        entityManager.clear();

        reviewReportUseCase.reviewReport(new ReviewReportCommand(
                reportId, ValidationAction.REJECT, null));

        assertFalse(reportRepository.existsById(reportId));
        assertFalse(notificationRepository.findByReport_Id(reportId).isPresent());
        assertFalse(userNotificationRepository.existsById(userNotificationId));
    }

    private UserEntity user(String email) {
        return userRepository.save(UserEntity.builder().email(email).password("test-password").build());
    }

    private CategoryEntity category() {
        CategoryEntity category = new CategoryEntity();
        category.setName("Test category");
        category.setDescription("Test category description");
        category.setTip("Test category tip");
        category.setEnabled(true);
        category.setRadius(5.0);
        return category;
    }

    private ReportEntity report(CategoryEntity category, UserEntity owner) {
        ReportEntity report = new ReportEntity();
        report.setId(UUID.randomUUID());
        report.setCategory(category);
        report.setStatus(ReportStatus.PENDING);
        report.setLatitude(37.9838);
        report.setLongitude(23.7275);
        report.setComments("Nearby report");
        report.setCreatedBy(owner);
        return report;
    }
}
