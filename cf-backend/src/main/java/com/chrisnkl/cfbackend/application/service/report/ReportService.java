package com.chrisnkl.cfbackend.application.service.report;

import com.chrisnkl.cfbackend.application.command.report.CreateReportCommand;
import com.chrisnkl.cfbackend.application.command.report.PageCommand;
import com.chrisnkl.cfbackend.application.result.pagination.PageResult;
import com.chrisnkl.cfbackend.application.port.in.report.CreateReportUseCase;
import com.chrisnkl.cfbackend.application.port.in.report.GetReportsUseCase;
import com.chrisnkl.cfbackend.application.port.out.report.CreateReportPort;
import com.chrisnkl.cfbackend.application.port.out.report.FindNearbyReportsPort;
import com.chrisnkl.cfbackend.application.result.report.CreateReportResult;
import com.chrisnkl.cfbackend.domain.exception.HttpBackendException;
import com.chrisnkl.cfbackend.domain.model.auth.User;
import com.chrisnkl.cfbackend.domain.model.report.Category;
import com.chrisnkl.cfbackend.domain.model.report.Report;
import com.chrisnkl.cfbackend.domain.model.report.ReportStatus;
import org.springframework.http.HttpStatus;

public class ReportService implements CreateReportUseCase, GetReportsUseCase {

    private final FindNearbyReportsPort findNearbyReportsPort;
    private final CreateReportPort createReportPort;

    public ReportService(FindNearbyReportsPort findNearbyReportsPort, CreateReportPort createReportPort) {
        this.findNearbyReportsPort = findNearbyReportsPort;
        this.createReportPort = createReportPort;
    }

    @Override
    public CreateReportResult createReport(CreateReportCommand command) {
        validateCoordinates(command.latitude(), command.longitude());

        Report report = Report.builder()
                .category(Category.builder().id(command.categoryId()).build())
                .status(ReportStatus.PENDING)
                .latitude(command.latitude())
                .longitude(command.longitude())
                .comments(command.comments())
                .photoUrl(command.photoUrl())
                .createdBy(User.builder().email(command.createdBy()).build())
                .build();

        return new CreateReportResult(createReportPort.createReport(report));
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
}
