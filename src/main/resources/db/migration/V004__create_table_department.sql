create table department
(
    id serial primary key,
    name varchar not null
);

alter table member
add department_id int references department (id);