package com.chrisnkl.cfbackend.domain.model.report;

import lombok.*;

import java.util.UUID;

@AllArgsConstructor @NoArgsConstructor
@Getter @Setter
@Builder
public class Category {

    private UUID id;
    private String name;
    private String description;
    private String tip;
    private boolean enabled;
    private double radius;
}
