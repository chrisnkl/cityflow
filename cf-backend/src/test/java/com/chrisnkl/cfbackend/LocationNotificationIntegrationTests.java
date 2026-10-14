package com.chrisnkl.cfbackend;

import com.chrisnkl.cfbackend.application.result.notification.ReportNotificationResult;
import com.chrisnkl.cfbackend.domain.model.report.Category;
import com.chrisnkl.cfbackend.domain.model.report.Report;
import com.chrisnkl.cfbackend.domain.model.report.ReportStatus;
import com.chrisnkl.cfbackend.domain.model.auth.User;
import com.chrisnkl.cfbackend.infrastructure.adapter.out.persistance.JpaReportAdapter;
import com.chrisnkl.cfbackend.infrastructure.adapter.out.persistance.JpaNotificationAdapter;
import com.chrisnkl.cfbackend.infrastructure.persistence.entity.auth.UserEntity;
import com.chrisnkl.cfbackend.infrastructure.persistence.entity.report.CategoryEntity;
import com.chrisnkl.cfbackend.infrastructure.persistence.entity.report.ReportEntity;
import com.chrisnkl.cfbackend.infrastructure.persistence.repository.JpaCategoryRepository;
import com.chrisnkl.cfbackend.infrastructure.persistence.repository.JpaNotificationRepository;
import com.chrisnkl.cfbackend.infrastructure.persistence.repository.JpaReportRepository;
import com.chrisnkl.cfbackend.infrastructure.persistence.repository.JpaUserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

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
    private JpaReportAdapter reportAdapter;

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
