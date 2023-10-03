CREATE TABLE users (
    id           bigint AUTO_INCREMENT PRIMARY KEY,
    username     varchar(255) NOT NULL,
    password     varchar(255) NOT NULL,
    created_at   timestamp,
    last_updated timestamp
);

CREATE TABLE authorities (
    id           bigint AUTO_INCREMENT PRIMARY KEY,
    name         varchar(255) NOT NULL UNIQUE,
    created_at   timestamp,
    last_updated timestamp
);

CREATE TABLE user_authorities (
    user_id      bigint,
    authority_id bigint,
    CONSTRAINT ua_user_fk
        FOREIGN KEY (user_id) REFERENCES users(id),
    CONSTRAINT ua_auth_fk
        FOREIGN KEY (authority_id) REFERENCES authorities(id)
);

INSERT
INTO users (id, username, password, created_at, last_updated)
VALUES (1, 'Latcho', '$2a$10$7tK423njgYWpPgflMVBQIeQAAmhYdYx/F.LbDO8IAwvuC7gS11Vb2', now(), now());

INSERT
INTO authorities (id, name, created_at, last_updated)
VALUES (1, 'hotels.create', now(), now());

INSERT
INTO authorities (id, name, created_at, last_updated)
VALUES (2, 'hotels.manage', now(), now());

INSERT
INTO user_authorities (user_id, authority_id)
VALUES (1, 1);

INSERT
INTO user_authorities (user_id, authority_id)
VALUES (1, 2);

CREATE TABLE hotels (
    id           bigint AUTO_INCREMENT PRIMARY KEY,
    name         varchar(255) NOT NULL,
    created_at   timestamp,
    last_updated timestamp
);

CREATE TABLE rooms (
    id            bigint AUTO_INCREMENT PRIMARY KEY,
    details       varchar(10) NOT NULL,
    max_occupants int         NOT NULL,
    hotel_id      bigint      NOT NULL,
    created_at    timestamp,
    last_updated  timestamp,
    CONSTRAINT room_hotel_fk
        FOREIGN KEY (hotel_id) REFERENCES hotels(id),
    CONSTRAINT unique_hotel_room
        UNIQUE (details, hotel_id)
);

CREATE TABLE bookings (
    id            bigint AUTO_INCREMENT PRIMARY KEY,
    occupants     int         NOT NULL,
    status        varchar(40) NOT NULL,
    hotel_id      bigint      NOT NULL,
    room_id       bigint      NOT NULL,
    user_id       bigint      NOT NULL,
    checkin_date  timestamp   NOT NULL,
    checkout_date timestamp   NOT NULL,
    created_at    timestamp,
    last_updated  timestamp,
    CONSTRAINT room_booking_fk
        FOREIGN KEY (room_id) REFERENCES rooms(id),
    CONSTRAINT hotel_booking_fk
        FOREIGN KEY (hotel_id) REFERENCES hotels(id),
    CONSTRAINT user_booking_fk
        FOREIGN KEY (user_id) REFERENCES users(id)
)

-- CREATE TABLE room_availability (
--     id           bigint AUTO_INCREMENT PRIMARY KEY,
--     on_date      timestamp NOT NULL,
--     room_id      bigint    NOT NULL,
--     availability int       NOT NULL,
--     CONSTRAINT room_availability_fk
--         FOREIGN KEY (room_id) REFERENCES rooms(id)
-- );