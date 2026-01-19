drop table users if exists;
drop table goods if exists;

create table users (
                       id bigint auto_increment,
                       name varchar(255),
                       create_time timestamp,
                       primary key (id)
);

create table goods (
                       id bigint auto_increment,
                       name varchar(255),
                       price bigint,
                       create_time timestamp,
                       update_time timestamp,
                       primary key (id)
);

insert into users (name, create_time) values ('Lili', now());
insert into users (name, create_time) values ('Fiona', now());

insert into goods (name, price, create_time, update_time) values ('bag', 2000, now(), now());
insert into goods (name, price, create_time, update_time) values ('bottole', 2500, now(), now());