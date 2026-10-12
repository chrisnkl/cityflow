package com.chrisnkl.cfbackend.application.service.report;

import com.chrisnkl.cfbackend.application.command.report.CreateReportCommand;
import com.chrisnkl.cfbackend.application.pagination.PageRequest;
import com.chrisnkl.cfbackend.application.pagination.PageResult;
import com.chrisnkl.cfbackend.application.port.in.report.CreateReportUseCase;
import com.chrisnkl.cfbackend.application.port.in.report.GetReportsUseCase;
import com.chrisnkl.cfbackend.application.port.out.report.FindNearbyReportsPort;
import com.chrisnkl.cfbackend.domain.model.report.Report;

public class ReportService implements CreateReportUseCase, GetReportsUseCase {

    private final FindNearbyReportsPort findNearbyReportsPort;

    public ReportService(FindNearbyReportsPort findNearbyReportsPort) {
        this.findNearbyReportsPort = findNearbyReportsPort;
    }

    @Override
    public void createReport(CreateReportCommand command) {
        // TODO: implement when report creation flows are ready.
    }

    @Override
    public PageResult<Report> getReports(double latitude, double longitude, PageRequest pageRequest) {
        validateCoordinates(latitude, longitude);

        return findNearbyReportsPort.findNearbyReports(latitude, longitude, pageRequest);
    }

    private void validateCoordinates(double latitude, double longitude) {
        if (!Double.isFinite(latitude) || !Double.isFinite(longitude)) {
            throw new IllegalArgumentException("Coordinates must be finite numbers.");
        }
        if (latitude < -90 || latitude > 90) {
            throw new IllegalArgumentException("Latitude must be between -90 and 90 degrees.");
        }

        if (longitude < -180 || longitude > 180) {
            throw new IllegalArgumentException("Longitude must be between -180 and 180 degrees.");
        }
    }
}
