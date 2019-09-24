insert into movies(title)
values ('Straszny film'),
       ('Karol. Papież, który pozostał człowiekiem'),
       ('Gwiezdne wojny');

insert into rooms(name, width, height)
values ('Dark room', 3, 1),
       ('Sala Królewska', 10, 10),
       ('Sala fajna', 10, 10);

insert into screenings(movie_id, room_id, time)
values (1, 1, timestamp '2119-09-13 10:20'),
       (1, 2, timestamp '2119-09-13 10:30'),
       (2, 2, timestamp '2119-09-13 11:55'),
       (3, 1, timestamp '2119-09-14 15:00'),
       (3, 3, timestamp '2119-09-13 12:00'),
       (2, 3, timestamp '2019-01-15 16:00');

insert into ticket_types(name, price)
values ('Adult', 25),
       ('Student', 18),
       ('Child', 12.5);

insert into reservations(screening_id, name, surname, created, seats)
values (3, 'Jan', 'Kowalski', timestamp '2019-01-15 16:00', '{55, 56}');

insert into screening_seats(screening_id, seat)
values (3, 55),
       (3, 56);

insert into reservation_tickets(reservation_id, ticket_type_id, quantity)
values (1, 1, 2);



