CREATE TABLE IF NOT EXISTS processed_events (
    event_id   UUID PRIMARY KEY,
    handled_at TIMESTAMPTZ NOT NULL DEFAULT now()
);
