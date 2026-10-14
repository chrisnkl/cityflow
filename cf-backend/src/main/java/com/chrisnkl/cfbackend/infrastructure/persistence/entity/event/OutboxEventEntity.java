package com.chrisnkl.cfbackend.infrastructure.persistence.entity.event;

import com.chrisnkl.cfbackend.domain.enums.EventType;
import com.chrisnkl.cfbackend.infrastructure.persistence.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "outbox_events")
@AllArgsConstructor
@NoArgsConstructor
@Getter @Setter
@Builder
public class OutboxEventEntity extends BaseEntity {

    @Id
    @Column(name = "event_id", nullable = false, updatable = false)
    private UUID eventId;

    @Column(name = "event_type", nullable = false, length = 255)
    private EventType eventType;

    @Column(name = "payload", nullable = false, columnDefinition = "TEXT")
    private String payload;
}
