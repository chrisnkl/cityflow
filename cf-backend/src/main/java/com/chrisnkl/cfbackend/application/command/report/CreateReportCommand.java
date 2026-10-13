package com.chrisnkl.cfbackend.application.command.report;

import java.util.UUID;

public record CreateReportCommand(

        UUID categoryId,
        String comments,
        String photoUrl,
        double latitude,
        double longitude,
        String createdBy

) { }
