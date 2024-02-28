alter session set container = XEPDB1;
create user test identified by test;
alter user test quota unlimited on users;
grant connect, resource to test;
create table test.users_tab (
    id number(19,0) generated as identity,
    name varchar2(255),
    create_at timestamp(6) with time zone,
    update_at timestamp(6) with time zone,
    gender number(1,0) check (gender in (0,1)),
    primary key (id)
);
create table test.outbox_tab (
    id raw(16) not null,
    message varchar2(255),
    version number(19,0),
    primary key (id)
);
