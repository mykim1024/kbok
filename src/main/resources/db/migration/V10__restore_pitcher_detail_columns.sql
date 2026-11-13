-- V10__restore_pitcher_detail_columns.sql
-- 선발·불펜 구분과 비교 조회에 필요해 V9에서 지운 세부기록 컬럼을 되살린다.
-- 기존 행은 0으로 채우고, 크롤링을 다시 돌려 실제 값으로 갱신한다.
ALTER TABLE pitcher_stat
    ADD COLUMN games_started INTEGER NOT NULL DEFAULT 0,
    ADD COLUMN svo           INTEGER NOT NULL DEFAULT 0,
    ADD COLUMN gdp           INTEGER NOT NULL DEFAULT 0;