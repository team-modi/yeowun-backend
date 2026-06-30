create table records (
    id bigint not null auto_increment,
    user_id bigint not null,
    exhibition_id bigint not null,
    write_mode varchar(20) not null,
    viewed_at date not null,
    content text not null,
    ai_summary text null,
    representative_emotion varchar(50) null,
    card_phrase varchar(255) null,
    ai_status varchar(20) not null,
    bookmarked bit not null,
    created_at datetime(6) not null,
    updated_at datetime(6) not null,
    deleted_at datetime(6) null,
    primary key (id)
) engine=InnoDB;

create table record_keywords (
    id bigint not null auto_increment,
    record_id bigint not null,
    keyword varchar(100) not null,
    source varchar(20) not null,
    primary key (id),
    constraint fk_record_keywords_record foreign key (record_id) references records (id)
) engine=InnoDB;

create table record_emotions (
    id bigint not null auto_increment,
    record_id bigint not null,
    emotion_code varchar(50) not null,
    primary key (id),
    constraint fk_record_emotions_record foreign key (record_id) references records (id)
) engine=InnoDB;

create table record_media (
    id bigint not null auto_increment,
    record_id bigint not null,
    type varchar(20) not null,
    url varchar(2048) not null,
    sort_order integer not null,
    size_bytes bigint not null,
    primary key (id),
    constraint fk_record_media_record foreign key (record_id) references records (id)
) engine=InnoDB;

create index idx_records_user_created on records (user_id, created_at);
create index idx_records_user_viewed on records (user_id, viewed_at);
create index idx_record_emotions_code on record_emotions (emotion_code);
create index idx_record_keywords_keyword on record_keywords (keyword);
