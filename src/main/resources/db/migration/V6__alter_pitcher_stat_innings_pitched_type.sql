-- V6__alter_pitcher_stat_innings_pitched_type.sql
ALTER TABLE pitcher_stat
    ALTER COLUMN innings_pitched TYPE VARCHAR(20)
    USING innings_pitched::VARCHAR;