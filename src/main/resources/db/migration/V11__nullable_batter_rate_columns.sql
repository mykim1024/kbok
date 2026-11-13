-- V11__nullable_batter_rate_columns.sql
-- 타율·출루율·장타율·OPS는 분모가 0이면 KBO가 "-"로 표시한다 (타수 0, 희생번트만 있는 타석 등).
-- 계산할 수 없는 값을 0으로 저장하면 진짜 기록처럼 보이고 평균 계산에도 섞이므로 NULL을 허용한다.
ALTER TABLE batter_stat
    ALTER COLUMN avg DROP NOT NULL,
    ALTER COLUMN obp DROP NOT NULL,
    ALTER COLUMN slg DROP NOT NULL,
    ALTER COLUMN ops DROP NOT NULL;
