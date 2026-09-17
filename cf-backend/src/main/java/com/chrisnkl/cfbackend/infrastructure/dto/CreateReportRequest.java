package com.chrisnkl.cfbackend.infrastructure.dto;

import com.chrisnkl.cfbackend.domain.exception.report.ReportCreationException;

import java.util.UUID;

public record CreateReportRequest(

        UUID categoryId,
        String comments,
        String photoUrl

) {

   public CreateReportRequest {
       if (categoryId == null || comments == null || photoUrl == null) throw new ReportCreationException("Failed to create report: categoryId, comments, and photoUrl must not be null");
   }

}
