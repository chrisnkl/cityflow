package com.chrisnkl.cfbackend.infrastructure.persistence.entity.report;

import com.chrisnkl.cfbackend.domain.model.report.ReportStatus;
import com.chrisnkl.cfbackend.infrastructure.persistence.entity.AuditedEntity;
import com.chrisnkl.cfbackend.infrastructure.persistence.entity.notification.NotificationEntity;
import jakarta.persistence.*;
import lombok.Getter;

import java.util.UUID;

@Entity
@Table(name = "reports")
@Getter
public class ReportEntity extends AuditedEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "category_id", nullable = false)
    private CategoryEntity category;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private ReportStatus status;

    @Column(name = "longitude", nullable = false)
    private double longitude;

    @Column(name = "latitude", nullable = false)
    private double latitude;

    @Lob
    @Column(name = "comments", columnDefinition = "TEXT")
    private String comments;

    @Column(name = "photo_url")
    private String photoUrl;

    @Column(name = "priority")
    private Double priority;

    @OneToOne(mappedBy = "report", cascade = CascadeType.ALL)
    private NotificationEntity notification;

}
