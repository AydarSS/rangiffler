create table if not exists rangiffler_geo.country
(
    id                      BINARY(16)  DEFAULT (UUID_TO_BIN(UUID())),
    code                    varchar(50)        not null,
    name                    varchar(255)       not null,
    flag                    longblob           not null,
    primary key (id)
);

