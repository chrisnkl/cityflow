package com.chrisnkl.cfbackend.domain.model.report;

import lombok.*;

import java.time.Instant;
import java.util.Set;
import java.util.UUID;

@AllArgsConstructor @NoArgsConstructor
@Getter @Setter
@Builder
public class Notification {

    private UUID id;
    private Set<Report> reports;
    private Instant createdAt;

}
