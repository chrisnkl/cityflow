package com.chrisnkl.cfbackend.infrastructure.persistence.entity.auth;

import com.chrisnkl.cfbackend.infrastructure.persistence.entity.BaseEntity;
import com.chrisnkl.cfbackend.infrastructure.persistence.entity.report.ReportEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import lombok.*;

import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "users")
@AllArgsConstructor @NoArgsConstructor
@Getter @Setter
@Builder
public class UserEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Email
    @Column(nullable = false, unique = true, length = 255)
    private String email;

    @Column(nullable = false, length = 255)
    private String password;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "user_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private Set<RoleEntity> roles;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "createdBy", cascade = CascadeType.ALL)
    private Set<ReportEntity> reports;


}
