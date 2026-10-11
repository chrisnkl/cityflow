package com.chrisnkl.cfbackend.infrastructure.persistence.mapper;

import com.chrisnkl.cfbackend.domain.model.auth.Role;
import com.chrisnkl.cfbackend.infrastructure.persistence.entity.auth.RoleEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {PermissionPersistenceMapper.class})
public interface RolePersistenceMapper {

    @Mapping(target = "users", ignore = true)
    RoleEntity toEntity(Role role);

    Role toDomain(RoleEntity entity);

}
