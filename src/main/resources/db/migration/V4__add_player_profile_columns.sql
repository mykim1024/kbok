-- V4__add_player_profile_columns.sql
ALTER TABLE player ADD COLUMN position VARCHAR(10);
ALTER TABLE player ADD COLUMN birth_date DATE;
ALTER TABLE player ADD COLUMN height INTEGER;
ALTER TABLE player ADD COLUMN weight INTEGER;
ALTER TABLE player ADD COLUMN school VARCHAR(100);
ALTER TABLE player ADD COLUMN back_number VARCHAR(5);