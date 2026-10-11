package com.chrisnkl.cfbackend.infrastructure.persistence.repository;

import com.chrisnkl.cfbackend.infrastructure.persistence.entity.auth.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface JpaUserRepository extends JpaRepository<UserEntity, UUID> {

    Optional<UserEntity> findByEmail(String email);

    @Query("""

    SELECT DISTINCT u
    FROM UserEntity u
    LEFT JOIN FETCH u.roles r
    LEFT JOIN FETCH r.permissions
    WHERE u.email = :email

    """)
    Optional<UserEntity> findByEmailWithRolesAndPermissions(@Param("email") String email);
}
