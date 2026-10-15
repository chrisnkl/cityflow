package com.chrisnkl.cfbackend.application.command.report;

public record UpdateSystemParametersCommand(
        double defaultRadiusMeters,
        double weightCount,
        double weightProximity,
        double weightSeverity
) {
}
