package com.chrisnkl.cfbackend.infrastructure.dto.request.report;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

public record SystemParametersRequest(
        @NotNull(message = "defaultRadiusMeters is required")
        @DecimalMin(value = "0.1", message = "defaultRadiusMeters must be greater than 0")
        Double defaultRadiusMeters,

        @NotNull(message = "weightCount is required")
        @DecimalMin(value = "0.0", message = "weightCount cannot be negative")
        Double weightCount,

        @NotNull(message = "weightProximity is required")
        @DecimalMin(value = "0.0", message = "weightProximity cannot be negative")
        Double weightProximity,

        @NotNull(message = "weightSeverity is required")
        @DecimalMin(value = "0.0", message = "weightSeverity cannot be negative")
        Double weightSeverity
) {
}
