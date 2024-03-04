create role test with login superuser password 'test';
--drop sequence users_tab_seq;
--drop table users_tab;
--drop table outbox_tab;
create table users_tab (
    id bigint primary key,
    create_at timestamp(6) with time zone,
    name character varying(255),
    update_at timestamp(6) with time zone,
    gender boolean
);
create sequence users_tab_seq start with 1 increment by 50;
create table outbox_tab (
    id uuid not null,
    create_at timestamp(6) with time zone,
    method smallint check (method between 0 and 7),
    message varchar(255),
    version bigint,
    primary key (id)
);

