package com.chrisnkl.cfbackend.application.command.report;

import java.util.UUID;

public record GetUserStatsCommand(
        String userEmail,
        String timeFilter,
        UUID categoryId,
        String status
) {
}
