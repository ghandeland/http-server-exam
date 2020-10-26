CREATE TYPE task_status AS ENUM ('OPEN', 'IN_PROGRESS', 'FINISHED', 'CANCELED');

create table task
(
    id          serial primary key,
    name        varchar,
    description varchar,
    status      task_status
);