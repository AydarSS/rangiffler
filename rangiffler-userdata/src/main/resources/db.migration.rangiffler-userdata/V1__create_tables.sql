create table if not exists rangiffler_userdata.country
(
    id                      binary(16) unique not null default (UUID_TO_BIN(UUID(), true)),
    code                    varchar(50)        not null,
    name                    varchar(255)       not null,
    flag                    longblob           not null,
    primary key (id)
);

create table if not exists rangiffler_userdata.user
(
    id                      binary(16) unique  not null default (UUID_TO_BIN(UUID(), true)),
    username                varchar(50) unique not null,
    firstname               varchar(255),
    lastname                varchar(255),
    avatar                  longblob,
    country_id              binary(16)         not null,
    primary key (id),
    constraint us_country_id foreign key (country_id) references country (id)
);

create table if not exists rangiffler_userdata.friendship
(
    requester_id binary(16) not null,
    addressee_id binary(16) not null,
    created_date datetime not null,
    status varchar(50) not null,
    primary key (requester_id, addressee_id),
    constraint friend_are_distinct_ck check (requester_id <> addressee_id),
    constraint fk_requester_id foreign key (requester_id) references user (id),
    constraint fk_addressee_id foreign key (addressee_id) references user (id)
);
