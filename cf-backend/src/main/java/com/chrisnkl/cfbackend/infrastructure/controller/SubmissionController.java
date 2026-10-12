package com.chrisnkl.cfbackend.infrastructure.controller;

import com.chrisnkl.cfbackend.application.port.in.report.CreateReportUseCase;
import com.chrisnkl.cfbackend.application.port.in.report.GetReportsUseCase;
import com.chrisnkl.cfbackend.application.pagination.PageRequest;
import com.chrisnkl.cfbackend.application.pagination.PageResult;
import com.chrisnkl.cfbackend.domain.model.report.Report;
import com.chrisnkl.cfbackend.infrastructure.common.ApiResponse;
import com.chrisnkl.cfbackend.infrastructure.dto.request.report.CreateReportRequest;
import com.chrisnkl.cfbackend.infrastructure.dto.response.report.ReportPageResponse;
import com.chrisnkl.cfbackend.infrastructure.mapper.ReportResponseMapper;
import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value =  "/api/{v}/secure/submission")
@RequiredArgsConstructor
public class SubmissionController {

    private final CreateReportUseCase createReportUseCase;
    private final GetReportsUseCase getReportsUseCase;
    private final ReportResponseMapper reportResponseMapper;

    @PostMapping
    public ResponseEntity<?> createReportSubmission(@RequestBody @Valid CreateReportRequest request) {
        return ResponseEntity.ok("");
    }

    @GetMapping
    @PreAuthorize("hasAuthority('VIEW_ALL_REPORTS') or hasAuthority('VIEW_OWN_REPORTS')")
    public ResponseEntity<ApiResponse<ReportPageResponse>> getReports(
            @RequestParam @DecimalMin("-90.0") @DecimalMax("90.0") double latitude,
            @RequestParam @DecimalMin("-180.0") @DecimalMax("180.0") double longitude,
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "20") @Min(1) @Max(PageRequest.MAX_PAGE_SIZE) int size,
            Authentication authentication
    ) {

        String email = authentication.getName();

        PageResult<Report> reports = getReportsUseCase.getReports(
                email,
                latitude,
                longitude,
                new PageRequest(page, size)
        );
        return ResponseEntity.ok(ApiResponse.success(
                "Reports retrieved successfully",
                reportResponseMapper.toPageResponse(reports)
        ));
    }

}
