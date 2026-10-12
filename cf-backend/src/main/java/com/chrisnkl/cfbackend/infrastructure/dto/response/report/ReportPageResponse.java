package com.chrisnkl.cfbackend.infrastructure.dto.response.report;

import java.util.List;

public record ReportPageResponse(
        List<ReportResponse> content,
        int page,
        int size,
        long totalElements,
        int totalPages
) {

    public ReportPageResponse {
        content = List.copyOf(content);
    }
}
