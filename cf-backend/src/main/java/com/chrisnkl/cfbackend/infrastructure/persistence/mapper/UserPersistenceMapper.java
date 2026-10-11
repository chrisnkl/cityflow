package com.chrisnkl.cfbackend.infrastructure.persistence.mapper;

import com.chrisnkl.cfbackend.domain.model.auth.User;
import com.chrisnkl.cfbackend.infrastructure.persistence.entity.auth.UserEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {RolePersistenceMapper.class, ReportPersistenceMapper.class})
public interface UserPersistenceMapper {

    @Mapping(target = "password", ignore = true)
    UserEntity toEntity(User user);

    User toDomain(UserEntity entity);

}
