-- V9__drop_pitcher_detail_columns.sql
-- 투수 세부기록(선발·세이브 기회·병살) 수집을 제거하면서 해당 컬럼도 삭제한다.
-- 해석 지표(ERA, WHIP, 탈삼진, 9이닝당 볼넷, 이닝)에 쓰지 않고,
-- 수집이 안 되는 상태로 0을 채워 두면 실제 기록처럼 오해될 수 있기 때문이다.
ALTER TABLE pitcher_stat
    DROP COLUMN games_started,
    DROP COLUMN svo,
    DROP COLUMN gdp;
