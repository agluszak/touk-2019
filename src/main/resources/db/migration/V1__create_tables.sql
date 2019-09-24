create table movies
(
    id    serial primary key,
    title varchar(100) not null
);

create table rooms
(
    id     serial primary key,
    name   varchar(100) not null,
    width  int          not null,
    height int          not null
);

create table screenings
(
    id       serial primary key,
    movie_id int references movies (id) not null,
    room_id  int references rooms (id)  not null,
    time     timestamp                  not null
);

create table screening_seats
(
    screening_id int references screenings (id) not null,
    seat         int                            not null
);

create table reservations
(
    id           serial primary key,
    screening_id int references screenings (id) not null,
    name         varchar(100)                   not null,
    surname      varchar(100)                   not null,
    created      timestamp                      not null,
    seats        int[]                          not null
);

create table ticket_types
(
    id    serial primary key,
    name  varchar(100)     not null,
    price double precision not null
);

create table reservation_tickets
(
    reservation_id int references reservations (id) not null,
    ticket_type_id int references ticket_types (id) not null,
    quantity       int                              not null
);
