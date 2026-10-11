package com.chrisnkl.cfbackend.infrastructure.persistence.mapper;

import com.chrisnkl.cfbackend.domain.model.auth.User;
import com.chrisnkl.cfbackend.infrastructure.persistence.entity.auth.UserEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

// Reports are NOT mapped here to avoid a circular dependency with ReportPersistenceMapper.
// Use ReportPersistenceMapper directly when you need to map reports for a user.
@Mapper(componentModel = "spring", uses = {RolePersistenceMapper.class})
public interface UserPersistenceMapper {

    @Mapping(target = "password", ignore = true)
    @Mapping(target = "reports", ignore = true)
    UserEntity toEntity(User user);

    @Mapping(target = "reports", ignore = true)
    User toDomain(UserEntity entity);

}
