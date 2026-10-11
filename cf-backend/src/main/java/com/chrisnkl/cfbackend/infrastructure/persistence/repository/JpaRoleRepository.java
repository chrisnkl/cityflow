package com.chrisnkl.cfbackend.infrastructure.persistence.repository;

import com.chrisnkl.cfbackend.infrastructure.persistence.entity.auth.RoleEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface JpaRoleRepository extends JpaRepository<RoleEntity, UUID> {
}
