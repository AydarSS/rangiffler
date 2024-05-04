create table if not exists rangiffler_photo.photo
(
    id                      BINARY(16)  DEFAULT (UUID_TO_BIN(UUID())),
    username                varchar(50) not null,
    photo                   longblob    not null,
    country_code            varchar(50) not null,
    created_date            datetime    not null,
    description             varchar(255),
    primary key (id)
);

create table if not exists rangiffler_photo.photo_likes
(
    id                      BINARY(16)  DEFAULT (UUID_TO_BIN(UUID())),
    user_id                 BINARY(16)  DEFAULT (UUID_TO_BIN(UUID())),
    username                varchar(50) not null,
    photo_id                BINARY(16)  DEFAULT (UUID_TO_BIN(UUID())),
    created_date            datetime    not null,
    constraint ph_photo_id foreign key (photo_id) references photo (id)
);
