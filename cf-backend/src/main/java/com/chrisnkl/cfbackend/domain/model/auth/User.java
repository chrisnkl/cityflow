package com.chrisnkl.cfbackend.domain.model.auth;

import com.chrisnkl.cfbackend.domain.model.report.Report;
import lombok.*;

import java.util.Set;
import java.util.UUID;

@AllArgsConstructor @NoArgsConstructor
@Getter @Setter
@Builder
public class User {

    private UUID id;
    private String email;
    private Set<Role> roles;
    private Set<Report> reports;

}
