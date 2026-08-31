CREATE INDEX IF NOT EXISTS idx_bookings_room_period ON bookings(room_id, start_at, end_at);
CREATE INDEX IF NOT EXISTS idx_bookings_user_period ON bookings(user_id, start_at);
CREATE INDEX IF NOT EXISTS idx_rooms_options_option ON rooms_options(option_id);
CREATE INDEX IF NOT EXISTS idx_rooms_floor ON rooms(floor);
CREATE INDEX IF NOT EXISTS idx_rooms_capacity ON rooms(capacity);
CREATE UNIQUE INDEX IF NOT EXISTS idx_options_name_unique ON options(name);