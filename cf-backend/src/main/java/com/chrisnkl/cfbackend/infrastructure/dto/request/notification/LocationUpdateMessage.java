package com.chrisnkl.cfbackend.infrastructure.dto.request.notification;

import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record LocationUpdateMessage(
        @NotBlank String type,
        @NotNull @Valid Location location
) {

    public record Location(
            @NotNull @DecimalMin("-90.0") @DecimalMax("90.0") Double lat,
            @NotNull @DecimalMin("-180.0") @DecimalMax("180.0") Double lon
    ) {
    }
}
