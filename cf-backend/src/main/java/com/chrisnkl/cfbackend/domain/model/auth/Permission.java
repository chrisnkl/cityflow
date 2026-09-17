package com.chrisnkl.cfbackend.domain.model.auth;

import lombok.*;

import java.util.UUID;

@AllArgsConstructor @NoArgsConstructor
@Getter @Setter
@Builder
public class Permission {

    private UUID id;
    private String name;

}
