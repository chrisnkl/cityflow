package com.chrisnkl.cfbackend.infrastructure.persistence.mapper;

import com.chrisnkl.cfbackend.domain.model.report.Notification;
import com.chrisnkl.cfbackend.infrastructure.persistence.entity.notification.NotificationEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {ReportPersistenceMapper.class})
public interface NotificationPersistenceMapper {

    @Mapping(target = "report", ignore = true)
    @Mapping(target = "reports", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    NotificationEntity toEntity(Notification notification);

    @Mapping(target = "reports", ignore = true)
    Notification toDomain(NotificationEntity entity);

}
