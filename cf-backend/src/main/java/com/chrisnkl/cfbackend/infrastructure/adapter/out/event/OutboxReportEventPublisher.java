package com.chrisnkl.cfbackend.infrastructure.adapter.out.event;

import com.chrisnkl.cfbackend.application.port.out.report.ReportCreatedEventPort;
import com.chrisnkl.cfbackend.domain.event.ReportCreatedEvent;
import com.chrisnkl.cfbackend.infrastructure.persistence.entity.event.OutboxEventEntity;
import com.chrisnkl.cfbackend.infrastructure.persistence.repository.JpaOutboxEventRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.springframework.stereotype.Repository;

@Repository
public class OutboxReportEventPublisher implements ReportCreatedEventPort {

    private final JpaOutboxEventRepository outboxEventRepository;
    private final ObjectMapper objectMapper = new ObjectMapper()
            .findAndRegisterModules()
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

    public OutboxReportEventPublisher(JpaOutboxEventRepository outboxEventRepository) {
        this.outboxEventRepository = outboxEventRepository;
    }

    @Override
    public void save(ReportCreatedEvent event) {
        try {
            outboxEventRepository.save(new OutboxEventEntity(
                    event.eventId(),
                    event.eventType(),
                    objectMapper.writeValueAsString(event)
            ));
        } catch (JsonProcessingException serializationFailure) {
            throw new IllegalStateException("Could not serialize report-created event.", serializationFailure);
        }
    }
}
