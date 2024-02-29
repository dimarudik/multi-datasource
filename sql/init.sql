create role test with login superuser password 'test';
create table users_tab (
    id bigint primary key,
    create_at timestamp(6) with time zone,
    name character varying(255),
    update_at timestamp(6) with time zone,
    gender boolean
);


