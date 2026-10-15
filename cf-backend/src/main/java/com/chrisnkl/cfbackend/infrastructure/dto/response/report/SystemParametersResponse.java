package com.chrisnkl.cfbackend.infrastructure.dto.response.report;

public record SystemParametersResponse(
        double defaultRadiusMeters,
        double weightCount,
        double weightProximity,
        double weightSeverity
) {
}
