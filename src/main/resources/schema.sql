drop table if exists films, users, genre_film, genre, ratingMPA, like_film, friends;


create table if not exists genre (
    id_genre integer generated by default as identity not null primary key,
    name_genre VARCHAR(20) NOT NULL
);

create table if not exists ratingMPA (
    id_ratingMPA integer generated by default as identity not null primary key,
    name_MPA VARCHAR(20) Not null
);

create table if not exists films (
    id integer generated by default as identity not null primary key,
    duration INTEGER NOT NULL,
    name VARCHAR(150) NOT NULL,
    description VARCHAR(600) NOT NULL,
    rating INTEGER,
    release_date TIMESTAMP NOT NULL,
    id_ratingMPA INTEGER not null references ratingMPA (id_ratingMPA) on delete cascade on update cascade
);

create table if not exists users (
    id integer generated by default as identity not null primary key,
    name VARCHAR(100),
    email VARCHAR(100) NOT NULL,
    login VARCHAR(50) NOT NULL,
    birthday TIMESTAMP NOT NULL
);

create unique index if not exists USER_EMAIL_UINDEX on users (email);
create unique index if not exists USER_LOGIN_UINDEX on users (login);

create table if not exists like_film (
    id_film integer not null references films (id) on delete cascade on update cascade,
    id_user integer not null references users (id) on delete cascade on update cascade,
    primary key (id_film, id_user)
);


create table if not exists friends (
    id_user integer not null references users (id) on delete cascade on update cascade,
    id_friend integer not null references users (id) on delete cascade on update cascade,
    unique (id_user, id_friend)
);

create table if not exists genre_film (
    id_film integer not null references films (id) on delete cascade on update cascade,
    id_genre integer not null references genre (id_genre) on delete cascade on update cascade,
    primary key (id_film, id_genre)
);

