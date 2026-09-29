package com.chrisnkl.cfbackend.infrastructure.persistence.entity.report;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Entity
@Table(name = "categories")
@AllArgsConstructor @NoArgsConstructor
@Getter @Setter
public class CategoryEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @Column(name = "name", nullable = false, unique = true, length = 255)
    private String name;

    @Column(name = "description", nullable = false, unique = true, length = 255)
    private String description;

    @Column(name = "tip", nullable = false, unique = true, length = 255)
    private String tip;

    @Column(name = "enabled", nullable = false)
    private boolean enabled;

    @Column(name = "radius")
    private double radius;

}
