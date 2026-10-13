package com.chrisnkl.cfbackend.infrastructure.dto.response.report;

import java.util.UUID;

public record CategoryResponse(

        UUID id,
        String name,
        String description,
        String tip,
        boolean enabled,
        double radius

) {
}
