package com.chrisnkl.cfbackend.infrastructure.persistence;

import com.chrisnkl.cfbackend.infrastructure.persistence.entity.auth.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface JpaUserRepository extends JpaRepository<UserEntity, UUID> {
}
