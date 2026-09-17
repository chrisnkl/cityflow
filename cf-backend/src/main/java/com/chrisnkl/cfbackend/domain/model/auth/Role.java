package com.chrisnkl.cfbackend.domain.model.auth;

import lombok.*;

import java.util.Set;
import java.util.UUID;

@AllArgsConstructor @NoArgsConstructor
@Getter @Setter
@Builder
public class Role {

    private UUID id;
    private String name;
    private Set<Permission> permissions;

}
