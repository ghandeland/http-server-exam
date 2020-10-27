create table task_member
(
    task_id integer not null,
    member_id integer not null,
    primary key (task_id, member_id)
);

alter table task_member
add constraint fk_tm_taskid
foreign key (task_id)
references task (id);

alter table task_member
add constraint fk_tm_memberid
foreign key (member_id)
references member (id);