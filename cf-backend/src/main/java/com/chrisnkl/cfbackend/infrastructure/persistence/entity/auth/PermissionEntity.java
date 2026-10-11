package com.chrisnkl.cfbackend.infrastructure.persistence.entity.auth;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "permissions")
@AllArgsConstructor @NoArgsConstructor
@Getter @Setter
public class PermissionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "code", unique = true, nullable = false, length = 255)
    private String code;

    @ManyToMany(mappedBy = "permissions", fetch = FetchType.LAZY)
    private Set<RoleEntity> roles = new HashSet<>();

}
