package com.chrisnkl.cfbackend.infrastructure.persistence.repository;

import com.chrisnkl.cfbackend.infrastructure.persistence.entity.notification.UserNotificationEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface JpaUserNotificationRepository extends JpaRepository<UserNotificationEntity, UUID> {

    boolean existsByNotification_IdAndUser_Id(UUID notificationId, UUID userId);

    @Query(
            value = """
                    SELECT userNotification
                    FROM UserNotificationEntity userNotification
                    JOIN FETCH userNotification.notification notification
                    JOIN FETCH notification.report report
                    JOIN FETCH report.category category
                    WHERE userNotification.user.email = :email
                      AND (:unreadOnly = FALSE OR userNotification.read = FALSE)
                    ORDER BY userNotification.createdAt DESC
                    """,
            countQuery = """
                    SELECT COUNT(userNotification)
                    FROM UserNotificationEntity userNotification
                    WHERE userNotification.user.email = :email
                      AND (:unreadOnly = FALSE OR userNotification.read = FALSE)
                    """
    )
    Page<UserNotificationEntity> findForUser(
            @Param("email") String email,
            @Param("unreadOnly") boolean unreadOnly,
            Pageable pageable
    );

    @Modifying
    @Query("""
            UPDATE UserNotificationEntity userNotification
            SET userNotification.read = TRUE
            WHERE userNotification.user.email = :email
              AND userNotification.read = FALSE
              AND userNotification.id IN :notificationIds
            """)
    int markReadForUser(@Param("email") String email, @Param("notificationIds") List<UUID> notificationIds);

    @Modifying
    @Query("""
            UPDATE UserNotificationEntity userNotification
            SET userNotification.read = TRUE
            WHERE userNotification.user.email = :email
              AND userNotification.read = FALSE
            """)
    int markAllReadForUser(@Param("email") String email);
}
