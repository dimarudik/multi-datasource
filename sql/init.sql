create database shard1;
\c shard1
create role shard1 with login superuser password 'shard1';
create table users_tab (
    id bigint primary key generated always as identity,
    create_at timestamp(6) with time zone,
    name character varying(255),
    update_at timestamp(6) with time zone
);
create database shard2;
\c shard2
create role shard2 with login superuser password 'shard2';
create table users_tab (
    id bigint primary key generated always as identity,
    create_at timestamp(6) with time zone,
    name character varying(255),
    update_at timestamp(6) with time zone
);