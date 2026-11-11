CREATE TABLE tournamentmgmt.entry_books (
    tournament_id BIGINT PRIMARY KEY REFERENCES tournamentmgmt.tournaments(id),
    intake_open BOOLEAN NOT NULL DEFAULT FALSE,
    version BIGINT NOT NULL DEFAULT 0 CHECK (version >= 0)
);

CREATE TABLE tournamentmgmt.entry_records (
    tournament_id BIGINT NOT NULL REFERENCES tournamentmgmt.entry_books(tournament_id),
    entry_id BIGINT NOT NULL CHECK (entry_id > 0),
    discipline_id BIGINT NOT NULL,
    discipline_name TEXT NOT NULL,
    bracket_id VARCHAR(100) NOT NULL,
    bracket_name TEXT NOT NULL,
    team_size VARCHAR(10) NOT NULL CHECK (team_size IN ('SINGLES', 'DOUBLES')),
    state VARCHAR(10) NOT NULL CHECK (state IN ('RECEIVED', 'ACCEPTED', 'REJECTED', 'WITHDRAWN')),
    PRIMARY KEY (tournament_id, entry_id)
);

CREATE TABLE tournamentmgmt.entry_members (
    tournament_id BIGINT NOT NULL,
    entry_id BIGINT NOT NULL,
    ordinal INTEGER NOT NULL CHECK (ordinal IN (0, 1)),
    player_key VARCHAR(64) COLLATE "C" NOT NULL,
    display_name VARCHAR(200) NOT NULL,
    PRIMARY KEY (tournament_id, entry_id, ordinal),
    UNIQUE (tournament_id, entry_id, player_key),
    FOREIGN KEY (tournament_id, entry_id) REFERENCES tournamentmgmt.entry_records(tournament_id, entry_id)
);

CREATE TABLE tournamentmgmt.entry_active_members (
    tournament_id BIGINT NOT NULL,
    discipline_id BIGINT NOT NULL,
    player_key VARCHAR(64) COLLATE "C" NOT NULL,
    entry_id BIGINT NOT NULL,
    PRIMARY KEY (tournament_id, discipline_id, player_key),
    FOREIGN KEY (tournament_id, entry_id) REFERENCES tournamentmgmt.entry_records(tournament_id, entry_id)
);

CREATE TABLE tournamentmgmt.entry_audit (
    tournament_id BIGINT NOT NULL REFERENCES tournamentmgmt.entry_books(tournament_id),
    book_version BIGINT NOT NULL CHECK (book_version > 0),
    entry_id BIGINT,
    operation VARCHAR(16) NOT NULL,
    previous_state VARCHAR(10),
    resulting_state VARCHAR(10),
    actor_id BIGINT NOT NULL CHECK (actor_id > 0),
    occurred_at TIMESTAMPTZ NOT NULL,
    PRIMARY KEY (tournament_id, book_version),
    FOREIGN KEY (tournament_id, entry_id) REFERENCES tournamentmgmt.entry_records(tournament_id, entry_id)
);
