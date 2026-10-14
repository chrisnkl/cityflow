package com.chrisnkl.cfbackend.infrastructure.persistence.entity.notification;

import com.chrisnkl.cfbackend.infrastructure.persistence.entity.BaseEntity;
import com.chrisnkl.cfbackend.infrastructure.persistence.entity.report.ReportEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "notifications")
@AllArgsConstructor @NoArgsConstructor
@Getter
public class NotificationEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = jakarta.persistence.GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "report_id", nullable = false, unique = true)
    private ReportEntity report;

    @OneToMany(mappedBy = "notification", fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
    private Set<UserNotificationEntity> reports;

}
