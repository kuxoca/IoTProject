create table if not exists user_action
(
    id        bigserial primary key not null,
    chat_id   bigint                not null,
    lang      varchar(50),
    user_id   bigint                not null,
    user_name varchar(50),
    command   varchar(255)
);