package com.chrisnkl.cfbackend.application.command.report;

public record CreateCategoryCommand(
        String name,
        String description,
        String tip,
        boolean enabled,
        double radius
) {
}
