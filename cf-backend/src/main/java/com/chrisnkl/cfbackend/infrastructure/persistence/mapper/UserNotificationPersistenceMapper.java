package com.chrisnkl.cfbackend.infrastructure.persistence.mapper;

import com.chrisnkl.cfbackend.domain.model.report.UserNotification;
import com.chrisnkl.cfbackend.infrastructure.persistence.entity.notification.UserNotificationEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = {NotificationPersistenceMapper.class, UserPersistenceMapper.class})
public interface UserNotificationPersistenceMapper {

    UserNotificationEntity toEntity(UserNotification userNotification);

    UserNotification toDomain(UserNotificationEntity entity);

}
