-- 전시 도메인(03_전시.md). CATALOG(외부 API 수집)와 CUSTOM(사용자 등록)을 한 테이블로 다룬다.
-- external_id = 외부 원천 seq(동기화 upsert 기준키, CUSTOM은 null). owner_id = CUSTOM 등록자(CATALOG는 null).

create table exhibitions (
    id bigint not null auto_increment,
    type varchar(20) not null,
    external_id varchar(100) null,
    owner_id bigint null,
    title varchar(100) not null,
    place varchar(200) null,
    start_date date null,
    end_date date null,
    region varchar(20) null,
    category varchar(20) null,
    poster_url varchar(2048) null,
    description text null,
    operating_hours varchar(500) null,
    price varchar(500) null,
    detail_url varchar(2048) null,
    service_name varchar(200) null,
    gps_x double null,
    gps_y double null,
    created_at datetime(6) not null,
    updated_at datetime(6) not null,
    deleted_at datetime(6) null,
    primary key (id)
) engine=InnoDB;

-- 동기화 upsert 기준키(CATALOG). MySQL은 unique 인덱스에서 다중 NULL을 허용하므로 CUSTOM(null)과 공존한다.
create unique index uk_exhibitions_external_id on exhibitions (external_id);
-- 노출 필터(CATALOG 전체 / CUSTOM 본인)용.
create index idx_exhibitions_type_owner on exhibitions (type, owner_id);
-- 진행 중 필터(기간)용.
create index idx_exhibitions_dates on exhibitions (start_date, end_date);
