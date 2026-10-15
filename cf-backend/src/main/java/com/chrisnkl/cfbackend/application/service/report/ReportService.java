package com.chrisnkl.cfbackend.application.service.report;

import com.chrisnkl.cfbackend.application.command.report.CreateReportCommand;
import com.chrisnkl.cfbackend.application.command.report.PageCommand;
import com.chrisnkl.cfbackend.application.command.report.ReviewReportCommand;
import com.chrisnkl.cfbackend.application.result.pagination.PageResult;
import com.chrisnkl.cfbackend.application.port.in.report.CreateReportUseCase;
import com.chrisnkl.cfbackend.application.port.in.report.GetReportImageUseCase;
import com.chrisnkl.cfbackend.application.port.in.report.GetReportsUseCase;
import com.chrisnkl.cfbackend.application.port.in.report.ReviewReportUseCase;
import com.chrisnkl.cfbackend.application.port.out.report.CreateReportPort;
import com.chrisnkl.cfbackend.application.port.out.report.FindReportPort;
import com.chrisnkl.cfbackend.application.port.out.report.FindNearbyReportsPort;
import com.chrisnkl.cfbackend.application.port.out.report.ManageReportPort;
import com.chrisnkl.cfbackend.application.port.out.report.ReportImageStoragePort;
import com.chrisnkl.cfbackend.application.result.report.CreateReportResult;
import com.chrisnkl.cfbackend.application.result.report.ReportImageResult;
import com.chrisnkl.cfbackend.domain.exception.HttpBackendException;
import com.chrisnkl.cfbackend.domain.model.auth.User;
import com.chrisnkl.cfbackend.domain.model.report.Category;
import com.chrisnkl.cfbackend.domain.model.report.Report;
import com.chrisnkl.cfbackend.domain.model.report.ReportStatus;
import com.chrisnkl.cfbackend.application.command.report.GetUserStatsCommand;
import com.chrisnkl.cfbackend.application.port.in.report.GetUserStatsUseCase;
import com.chrisnkl.cfbackend.application.port.out.report.GetUserStatsPort;
import com.chrisnkl.cfbackend.application.result.report.UserStatsResult;
import org.springframework.http.HttpStatus;

import java.util.UUID;

public class ReportService implements CreateReportUseCase, GetReportsUseCase, GetReportImageUseCase, ReviewReportUseCase, GetUserStatsUseCase {

    private final FindNearbyReportsPort findNearbyReportsPort;
    private final CreateReportPort createReportPort;
    private final ReportImageStoragePort reportImageStoragePort;
    private final FindReportPort findReportPort;
    private final ManageReportPort manageReportPort;
    private final GetUserStatsPort getUserStatsPort;

    public ReportService(
            FindNearbyReportsPort findNearbyReportsPort,
            CreateReportPort createReportPort,
            ReportImageStoragePort reportImageStoragePort,
            FindReportPort findReportPort,
            ManageReportPort manageReportPort,
            GetUserStatsPort getUserStatsPort
    ) {
        this.findNearbyReportsPort = findNearbyReportsPort;
        this.createReportPort = createReportPort;
        this.reportImageStoragePort = reportImageStoragePort;
        this.findReportPort = findReportPort;
        this.manageReportPort = manageReportPort;
        this.getUserStatsPort = getUserStatsPort;
    }

    @Override
    public CreateReportResult createReport(CreateReportCommand command) {
        validateCoordinates(command.latitude(), command.longitude());
        validateImage(command.photoBytes(), command.photoContentType());

        UUID reportId = UUID.randomUUID();
        String photoUrl = command.photoBytes() == null ? null
                : reportImageStoragePort.upload(reportId, command.photoContentType(), command.photoBytes());

        Report report = Report.builder()
                .id(reportId)
                .category(Category.builder().id(command.categoryId()).build())
                .status(ReportStatus.PENDING)
                .latitude(command.latitude())
                .longitude(command.longitude())
                .comments(command.comments())
                .photoUrl(photoUrl)
                .createdBy(User.builder().email(command.createdBy()).build())
                .build();

        try {
            createReportPort.createReport(report);
        } catch (RuntimeException persistenceFailure) {
            if (photoUrl != null) {
                try {
                    reportImageStoragePort.delete(photoUrl);
                } catch (RuntimeException cleanupFailure) {
                    persistenceFailure.addSuppressed(cleanupFailure);
                }
            }
            throw persistenceFailure;
        }
        return new CreateReportResult(reportId);
    }

    @Override
    public PageResult<Report> getReports(String email, double latitude, double longitude, PageCommand pageCommand) {
        validateCoordinates(latitude, longitude);

        return findNearbyReportsPort.findNearbyReports(email, latitude, longitude, pageCommand);
    }

    private void validateCoordinates(double latitude, double longitude) {
        if (!Double.isFinite(latitude) || !Double.isFinite(longitude)) {
            throw new HttpBackendException(HttpStatus.BAD_REQUEST.value(), "Coordinates must be finite numbers.");
        }
        if (latitude < -90 || latitude > 90) {
            throw new HttpBackendException(HttpStatus.BAD_REQUEST.value(),"Latitude must be between -90 and 90 degrees.");
        }

        if (longitude < -180 || longitude > 180) {
            throw new HttpBackendException(HttpStatus.BAD_REQUEST.value(),"Longitude must be between -180 and 180 degrees.");
        }
    }

    @Override
    public ReportImageResult getReportImage(UUID reportId, String requesterEmail, boolean canViewAllReports) {
        Report report = findReportPort.findById(reportId)
                .orElseThrow(() -> new HttpBackendException(HttpStatus.NOT_FOUND.value(), "Report not found."));
        if (!canViewAllReports && (report.getCreatedBy() == null
                || !report.getCreatedBy().getEmail().equalsIgnoreCase(requesterEmail))) {
            throw new HttpBackendException(HttpStatus.FORBIDDEN.value(), "You are not allowed to view this report image.");
        }
        if (report.getPhotoUrl() == null) throw new HttpBackendException(HttpStatus.NOT_FOUND.value(), "Report has no image.");

        return reportImageStoragePort.download(report.getPhotoUrl());
    }

    @Override
    public void reviewReport(ReviewReportCommand command) {
        if (command.status() == null) {
            throw new HttpBackendException(HttpStatus.BAD_REQUEST.value(), "Report status is required.");
        }
        Report report = findReportPort.findById(command.reportId())
                .orElseThrow(() -> new HttpBackendException(HttpStatus.NOT_FOUND.value(), "Report not found."));

        if (report.getStatus() != null && report.getStatus().isFinal()) {
            throw new HttpBackendException(HttpStatus.BAD_REQUEST.value(),
                    "Cannot update report status because it is already finalized.");
        }

        manageReportPort.updateStatus(command.reportId(), command.status());
    }

    @Override
    public UserStatsResult getUserStats(GetUserStatsCommand command) {
        if (command == null || command.userEmail() == null || command.userEmail().isBlank()) {
            throw new HttpBackendException(HttpStatus.UNAUTHORIZED.value(), "An authenticated user is required.");
        }
        return getUserStatsPort.getUserStats(command);
    }

    private void validateImage(byte[] imageBytes, String contentType) {
        if (imageBytes == null && contentType == null) {
            return;
        }
        if (imageBytes == null || contentType == null || imageBytes.length == 0) {
            throw new HttpBackendException(HttpStatus.BAD_REQUEST.value(), "Photo bytes and content type must be provided together.");
        }
        if (!ReportImagePolicy.supportsContentType(contentType)) {
            throw new HttpBackendException(HttpStatus.BAD_REQUEST.value(), "Only JPEG, PNG, and WebP images are supported.");
        }
        if (imageBytes.length > ReportImagePolicy.MAX_SIZE_BYTES) {
            throw new HttpBackendException(HttpStatus.BAD_REQUEST.value(), "Image size must be between 1 byte and 10 MB.");
        }
    }
}
