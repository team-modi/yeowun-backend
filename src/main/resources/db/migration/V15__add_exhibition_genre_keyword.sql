-- 개인 전시(CUSTOM) 등록 시 부여하는 장르 키워드(임시 랜덤, 추후 AI가 대체). 상세의 keywords로 노출된다.
-- CATALOG는 null. 단일 문자열 컬럼.
alter table exhibitions add column genre_keyword varchar(50) null;
