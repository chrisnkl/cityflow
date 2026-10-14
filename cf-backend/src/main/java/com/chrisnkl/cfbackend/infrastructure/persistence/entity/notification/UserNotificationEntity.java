package com.chrisnkl.cfbackend.infrastructure.persistence.entity.notification;

import com.chrisnkl.cfbackend.infrastructure.persistence.entity.BaseEntity;
import com.chrisnkl.cfbackend.infrastructure.persistence.entity.auth.UserEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Table(
        name = "user_notifications",
        uniqueConstraints = @UniqueConstraint(
                name = "uq_user_notifications_user_notification",
                columnNames = {"user_id", "notification_id"}
        )
)
@AllArgsConstructor @NoArgsConstructor
@Getter
public class UserNotificationEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "notification_id", nullable = false)
    private NotificationEntity notification;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    @Column(name = "is_read", nullable = false, columnDefinition = "BOOLEAN DEFAULT FALSE")
    private boolean read = false;

}
