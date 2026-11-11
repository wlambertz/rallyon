CREATE TABLE tournamentmgmt.tournament_announcement (
    tournament_id BIGINT PRIMARY KEY REFERENCES tournamentmgmt.tournaments(id),
    title VARCHAR(200) NOT NULL,
    event_start TIMESTAMPTZ NOT NULL,
    event_end TIMESTAMPTZ NOT NULL,
    venue VARCHAR(500) NOT NULL,
    body TEXT NOT NULL,
    published BOOLEAN NOT NULL,
    version BIGINT NOT NULL,
    published_at TIMESTAMPTZ NOT NULL,
    CONSTRAINT chk_announcement_event_range CHECK (event_end >= event_start),
    CONSTRAINT chk_announcement_body_length CHECK (char_length(body) <= 10000),
    CONSTRAINT chk_announcement_version_nonnegative CHECK (version >= 0)
);
