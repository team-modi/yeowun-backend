-- 유저 실명(01_인증인가·02_유저): 소셜 동의항목 name(카카오 이름·구글 name). 미동의/미지원 시 null이라 nullable.
alter table users
    add column name varchar(50) null;
