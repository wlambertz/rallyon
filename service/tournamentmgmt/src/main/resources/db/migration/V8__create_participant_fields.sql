CREATE TABLE tournamentmgmt.participant_fields (
    tournament_id BIGINT NOT NULL REFERENCES tournamentmgmt.tournaments(id),
    discipline_id BIGINT NOT NULL CHECK (discipline_id > 0),
    bracket_id VARCHAR(100) COLLATE "C" NOT NULL,
    version BIGINT NOT NULL DEFAULT 0 CHECK (version >= 0),
    discipline_name TEXT,
    bracket_name TEXT,
    team_size VARCHAR(10) CHECK (team_size IN ('SINGLES', 'DOUBLES')),
    source_tournament_version BIGINT CHECK (source_tournament_version >= 0),
    source_entry_book_version BIGINT CHECK (source_entry_book_version >= 0),
    PRIMARY KEY (tournament_id, discipline_id, bracket_id),
    CHECK (
        (version = 0 AND discipline_name IS NULL AND bracket_name IS NULL AND team_size IS NULL
            AND source_tournament_version IS NULL AND source_entry_book_version IS NULL)
        OR (version > 0 AND discipline_name IS NOT NULL AND bracket_name IS NOT NULL AND team_size IS NOT NULL
            AND source_tournament_version IS NOT NULL AND source_entry_book_version IS NOT NULL)
    )
);

CREATE TABLE tournamentmgmt.participant_field_units (
    tournament_id BIGINT NOT NULL,
    discipline_id BIGINT NOT NULL,
    bracket_id VARCHAR(100) COLLATE "C" NOT NULL,
    entry_id BIGINT NOT NULL CHECK (entry_id > 0),
    discipline_name TEXT NOT NULL,
    bracket_name TEXT NOT NULL,
    team_size VARCHAR(10) NOT NULL CHECK (team_size IN ('SINGLES', 'DOUBLES')),
    PRIMARY KEY (tournament_id, discipline_id, bracket_id, entry_id),
    FOREIGN KEY (tournament_id, discipline_id, bracket_id)
        REFERENCES tournamentmgmt.participant_fields(tournament_id, discipline_id, bracket_id)
);

CREATE TABLE tournamentmgmt.participant_field_members (
    tournament_id BIGINT NOT NULL,
    discipline_id BIGINT NOT NULL,
    bracket_id VARCHAR(100) COLLATE "C" NOT NULL,
    entry_id BIGINT NOT NULL,
    ordinal INTEGER NOT NULL CHECK (ordinal IN (0, 1)),
    player_key VARCHAR(64) COLLATE "C" NOT NULL,
    display_name VARCHAR(200) NOT NULL,
    PRIMARY KEY (tournament_id, discipline_id, bracket_id, entry_id, ordinal),
    UNIQUE (tournament_id, discipline_id, bracket_id, player_key),
    FOREIGN KEY (tournament_id, discipline_id, bracket_id, entry_id)
        REFERENCES tournamentmgmt.participant_field_units(tournament_id, discipline_id, bracket_id, entry_id)
);

CREATE TABLE tournamentmgmt.participant_field_audit (
    tournament_id BIGINT NOT NULL,
    discipline_id BIGINT NOT NULL,
    bracket_id VARCHAR(100) COLLATE "C" NOT NULL,
    field_version BIGINT NOT NULL CHECK (field_version > 0),
    actor_id BIGINT NOT NULL CHECK (actor_id > 0),
    occurred_at TIMESTAMPTZ NOT NULL,
    operation VARCHAR(16) NOT NULL CHECK (operation = 'PREPARE'),
    unit_count INTEGER NOT NULL CHECK (unit_count >= 0),
    source_tournament_version BIGINT NOT NULL CHECK (source_tournament_version >= 0),
    source_entry_book_version BIGINT NOT NULL CHECK (source_entry_book_version >= 0),
    PRIMARY KEY (tournament_id, discipline_id, bracket_id, field_version),
    FOREIGN KEY (tournament_id, discipline_id, bracket_id)
        REFERENCES tournamentmgmt.participant_fields(tournament_id, discipline_id, bracket_id)
);
