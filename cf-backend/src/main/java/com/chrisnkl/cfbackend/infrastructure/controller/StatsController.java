package com.chrisnkl.cfbackend.infrastructure.controller;

import com.chrisnkl.cfbackend.application.command.report.GetUserStatsCommand;
import com.chrisnkl.cfbackend.application.port.in.report.GetUserStatsUseCase;
import com.chrisnkl.cfbackend.application.result.report.UserStatsResult;
import com.chrisnkl.cfbackend.infrastructure.common.ApiResponse;
import com.chrisnkl.cfbackend.infrastructure.dto.response.report.UserStatsResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/{v}/secure/stats")
@PreAuthorize("hasRole('USER') or hasAuthority('VIEW_STATISTICS')")
@RequiredArgsConstructor
public class StatsController {

    private final GetUserStatsUseCase getUserStatsUseCase;

    @GetMapping("/user")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<UserStatsResponse>> getUserStats(
            @RequestParam(required = false) String timeFilter,
            @RequestParam(required = false) UUID categoryId,
            @RequestParam(required = false) String status,
            Authentication authentication
    ) {
        GetUserStatsCommand command = new GetUserStatsCommand(
                authentication.getName(),
                timeFilter,
                categoryId,
                status
        );

        UserStatsResult result = getUserStatsUseCase.getUserStats(command);

        UserStatsResponse response = new UserStatsResponse(
                result.totalSubmitted(),
                result.totalValidated(),
                result.totalNotifications(),
                result.topCategories().stream()
                        .map(c -> new UserStatsResponse.CategoryStatResponse(c.categoryName(), c.count(), c.percentage()))
                        .toList(),
                result.statusBreakdown()
        );

        return ResponseEntity.ok(ApiResponse.success("User stats retrieved successfully", response));
    }
}
