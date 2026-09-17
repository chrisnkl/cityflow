package com.chrisnkl.cfbackend.infrastructure.controller;

import com.chrisnkl.cfbackend.application.port.in.report.CreateReportUseCase;
import com.chrisnkl.cfbackend.application.port.in.report.GetReportsUseCase;
import com.chrisnkl.cfbackend.infrastructure.dto.CreateReportRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value =  "/api/{v}/secure/submission")
@RequiredArgsConstructor
public class SubmissionController {

    private final CreateReportUseCase createReportUseCase;
    private final GetReportsUseCase getReportsUseCase;

    @PostMapping
    public ResponseEntity<?> createReportSubmission(@RequestBody @Valid CreateReportRequest request) {
        return ResponseEntity.ok("");
    }

}
