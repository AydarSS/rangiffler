create table if not exists rangiffler_auth.user
(
    id                      BINARY(16)  DEFAULT (UUID_TO_BIN(UUID())),
    username                varchar(50) unique not null,
    password                varchar(255)       not null,
    enabled                 boolean            not null,
    account_non_expired     boolean            not null,
    account_non_locked      boolean            not null,
    credentials_non_expired boolean            not null,
    primary key (id, username)
);

create table if not exists rangiffler_auth.authority
(
    id        BINARY(16)  DEFAULT (UUID_TO_BIN(UUID())),
    user_id   BINARY(16)  not null,
    authority varchar(50) not null,
    constraint fk_authorities_users foreign key (user_id) references user (id)
);

