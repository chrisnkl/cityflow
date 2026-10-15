package com.chrisnkl.cfbackend.application.command.report;

import java.util.UUID;

public record UpdateCategoryCommand(
        UUID id,
        String name,
        String description,
        String tip,
        boolean enabled,
        double radius
) {
}
