package com.chrisnkl.cfbackend.infrastructure.persistence.mapper;

import com.chrisnkl.cfbackend.domain.model.auth.Permission;
import com.chrisnkl.cfbackend.infrastructure.persistence.entity.auth.PermissionEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface PermissionPersistenceMapper {

    @Mapping(target = "roles", ignore = true)
    PermissionEntity toEntity(Permission permission);
    
    Permission toDomain(PermissionEntity entity);

}
