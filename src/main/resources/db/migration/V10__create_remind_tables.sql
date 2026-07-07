create table reminds (
    id bigint not null auto_increment,
    user_id bigint not null,
    record_id bigint not null,
    exhibition_id bigint not null,
    exhibition_title varchar(100) not null,
    exhibition_poster_url varchar(2048) null,
    exhibition_place varchar(200) null,
    record_viewed_at date null,
    reflection text not null,
    ai_summary text null,
    ai_status varchar(20) not null,
    created_at datetime(6) not null,
    updated_at datetime(6) not null,
    deleted_at datetime(6) null,
    primary key (id)
) engine=InnoDB;

create table remind_emotions (
    id bigint not null auto_increment,
    remind_id bigint not null,
    emotion_code varchar(50) not null,
    primary key (id),
    constraint fk_remind_emotions_remind foreign key (remind_id) references reminds (id)
) engine=InnoDB;

create index idx_reminds_user_created on reminds (user_id, created_at);
create index idx_reminds_record on reminds (record_id);
create index idx_remind_emotions_code on remind_emotions (emotion_code);
