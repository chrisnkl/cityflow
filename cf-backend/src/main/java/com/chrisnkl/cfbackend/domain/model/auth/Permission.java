package com.chrisnkl.cfbackend.domain.model.auth;

import lombok.*;

import java.time.Instant;
import java.util.UUID;

@AllArgsConstructor @NoArgsConstructor
@Getter @Setter
@Builder
public class Permission {

    private UUID id;
    private String name;
    private Instant createdAt;
    private Instant updatedAt;

}
