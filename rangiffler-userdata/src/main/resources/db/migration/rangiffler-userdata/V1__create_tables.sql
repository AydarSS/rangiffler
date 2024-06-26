create table if not exists rangiffler_userdata.user
(
    id                      BINARY(16)  DEFAULT (UUID_TO_BIN(UUID())),
    username                varchar(50) unique not null,
    firstname               varchar(255),
    lastname                varchar(255),
    avatar                  longblob,
    country_id              binary(16),
    primary key (id)
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
