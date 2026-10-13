package com.chrisnkl.cfbackend.infrastructure.dto.request.report;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record CreateReportRequest(

        @NotNull
        UUID categoryId,

        @NotBlank
        String comments,

        String photoUrl,

        @DecimalMin("-90.0") @DecimalMax("90.0")
        double latitude,

        @DecimalMin("-180.0") @DecimalMax("180.0")
        double longitude

) { }
