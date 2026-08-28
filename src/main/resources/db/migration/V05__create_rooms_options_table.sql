CREATE TABLE IF NOT EXISTS rooms_options (
    room_id   BIGINT NOT NULL REFERENCES rooms(id) ON DELETE CASCADE,
    option_id BIGINT NOT NULL REFERENCES options(id) ON DELETE CASCADE,
    PRIMARY KEY (room_id, option_id)
);