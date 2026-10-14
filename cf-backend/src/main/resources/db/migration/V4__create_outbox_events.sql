CREATE TABLE outbox_events (
    event_id UUID PRIMARY KEY,
    event_type VARCHAR(100) NOT NULL,
    payload TEXT NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    published_at TIMESTAMP WITH TIME ZONE
);

CREATE INDEX idx_outbox_events_pending
    ON outbox_events (created_at)
    WHERE published_at IS NULL;
