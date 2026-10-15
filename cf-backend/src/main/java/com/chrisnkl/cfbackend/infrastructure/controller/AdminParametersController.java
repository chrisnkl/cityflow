package com.chrisnkl.cfbackend.infrastructure.controller;

import com.chrisnkl.cfbackend.application.command.report.UpdateSystemParametersCommand;
import com.chrisnkl.cfbackend.application.port.in.report.ManageSystemParametersUseCase;
import com.chrisnkl.cfbackend.domain.model.report.SystemParameters;
import com.chrisnkl.cfbackend.infrastructure.common.ApiResponse;
import com.chrisnkl.cfbackend.infrastructure.dto.request.report.SystemParametersRequest;
import com.chrisnkl.cfbackend.infrastructure.dto.response.report.SystemParametersResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/{v}/secure/admin/parameters")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
@Slf4j
public class AdminParametersController {

    private final ManageSystemParametersUseCase manageSystemParametersUseCase;

    @GetMapping
    @PreAuthorize("hasAuthority('MANAGE_SYSTEM_SETTINGS')")
    public ResponseEntity<ApiResponse<SystemParametersResponse>> getParameters() {
        SystemParameters params = manageSystemParametersUseCase.getSystemParameters();
        SystemParametersResponse response = toResponse(params);
        return ResponseEntity.ok(ApiResponse.success("Parameters retrieved", response));
    }

    @PutMapping
    @PreAuthorize("hasAuthority('MANAGE_SYSTEM_SETTINGS')")
    public ResponseEntity<ApiResponse<SystemParametersResponse>> updateParameters(
            @Valid @RequestBody SystemParametersRequest request
    ) {
        log.info("Updating system parameters: defaultRadius={}, countWeight={}, proximityWeight={}, severityWeight={}",
                request.defaultRadiusMeters(), request.weightCount(), request.weightProximity(), request.weightSeverity());

        UpdateSystemParametersCommand command = new UpdateSystemParametersCommand(
                request.defaultRadiusMeters(),
                request.weightCount(),
                request.weightProximity(),
                request.weightSeverity()
        );

        SystemParameters updated = manageSystemParametersUseCase.updateSystemParameters(command);
        SystemParametersResponse response = toResponse(updated);
        return ResponseEntity.ok(ApiResponse.success("Parameters updated successfully", response));
    }

    private SystemParametersResponse toResponse(SystemParameters params) {
        return new SystemParametersResponse(
                params.getDefaultRadiusMeters(),
                params.getWeightCount(),
                params.getWeightProximity(),
                params.getWeightSeverity()
        );
    }
}
