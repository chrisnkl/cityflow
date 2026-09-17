package com.chrisnkl.cfbackend.domain.model.report;

import com.chrisnkl.cfbackend.domain.model.auth.User;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

@AllArgsConstructor @NoArgsConstructor
@Getter @Setter
@Builder
public class Report {

    private UUID id;
    private Category category;
    private ReportStatus status;
    private String comments;
    private String photoUrl;
    private Double priority;

    private Instant createdAt;
    private Instant updatedAt;

    private User createdBy;
    private User updatedBy;

}
