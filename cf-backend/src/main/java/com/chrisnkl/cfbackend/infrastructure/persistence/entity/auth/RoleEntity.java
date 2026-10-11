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
@Table(name = "roles")
@AllArgsConstructor @NoArgsConstructor
@Getter @Setter
public class RoleEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "name", unique = true, nullable = false, length = 255)
    private String name;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "role_permissions",
            joinColumns = @JoinColumn(name = "role_id"),
            inverseJoinColumns = @JoinColumn(name = "permission_id"),
            uniqueConstraints = @UniqueConstraint(
                    columnNames = {"role_id", "permission_id"}
            )
    )
    private Set<PermissionEntity> permissions = new HashSet<>();

    @ManyToMany(
            mappedBy = "roles",
            fetch = FetchType.LAZY
    )
    private Set<UserEntity> users = new HashSet<>();

}
