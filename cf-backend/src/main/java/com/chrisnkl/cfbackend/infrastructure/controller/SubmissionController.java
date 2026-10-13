package com.chrisnkl.cfbackend.infrastructure.controller;

import com.chrisnkl.cfbackend.application.port.in.report.CreateReportUseCase;
import com.chrisnkl.cfbackend.application.port.in.report.GetReportsUseCase;
import com.chrisnkl.cfbackend.application.command.report.PageCommand;
import com.chrisnkl.cfbackend.application.result.pagination.PageResult;
import com.chrisnkl.cfbackend.application.result.report.CreateReportResult;
import com.chrisnkl.cfbackend.domain.enums.PermissionCode;
import com.chrisnkl.cfbackend.domain.model.report.Report;
import com.chrisnkl.cfbackend.infrastructure.common.ApiResponse;
import com.chrisnkl.cfbackend.infrastructure.dto.request.report.CreateReportRequest;
import com.chrisnkl.cfbackend.infrastructure.dto.response.report.ReportPageResponse;
import com.chrisnkl.cfbackend.infrastructure.mapper.CreateReportCommandMapper;
import com.chrisnkl.cfbackend.infrastructure.mapper.ReportResponseMapper;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;
import java.util.UUID;

@RestController
@RequestMapping(value =  "/api/{v}/secure/submission")
@RequiredArgsConstructor
@Slf4j
public class SubmissionController {

    private final CreateReportUseCase createReportUseCase;
    private final GetReportsUseCase getReportsUseCase;
    private final ReportResponseMapper reportResponseMapper;
    private final CreateReportCommandMapper createReportCommandMapper;

    @GetMapping
    @PreAuthorize("hasAuthority('VIEW_ALL_REPORTS') or hasAuthority('VIEW_OWN_REPORTS')")
    public ResponseEntity<ApiResponse<ReportPageResponse>> getReports(
            @RequestParam @DecimalMin("-90.0") @DecimalMax("90.0") double latitude,
            @RequestParam @DecimalMin("-180.0") @DecimalMax("180.0") double longitude,
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "20") @Min(1) @Max(PageCommand.MAX_PAGE_SIZE) int size,
            @RequestParam(required = false) @Email String email,
            Authentication authentication
    ) {

        boolean canViewAll = authentication.getAuthorities().stream().anyMatch(authority -> Objects.equals(authority.getAuthority(), PermissionCode.VIEW_ALL_REPORTS.name()));
        if (email == null || email.isBlank()) email = authentication.getName();

        if (!canViewAll && !authentication.getName().equalsIgnoreCase(email)) throw new AccessDeniedException("User " + authentication.getName() + " tried accessing another user's reports.");

        PageResult<Report> reports = getReportsUseCase.getReports(
                email,
                latitude,
                longitude,
                new PageCommand(page, size)
        );
        return ResponseEntity.ok(ApiResponse.success(
                "Reports retrieved successfully",
                reportResponseMapper.toPageResponse(reports)
        ));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('REPORT_ISSUE')")
    public ResponseEntity<ApiResponse<UUID>> createReport(
            @Valid @RequestBody CreateReportRequest request,
            Authentication authentication
    ) {

        CreateReportResult result = createReportUseCase.createReport(
                createReportCommandMapper.toCommand(request, authentication.getName())
        );
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.created("Report created successfully", result.reportId()));
    }


}
