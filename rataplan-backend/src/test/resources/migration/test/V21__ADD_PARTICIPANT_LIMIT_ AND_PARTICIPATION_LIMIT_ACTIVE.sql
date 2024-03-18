alter table voteoption
    add column participantlimitactive
        boolean not null default false;

alter table voteoption
    add column participantlimit
        integer default null;

alter table voteoption
    alter column participantlimitactive
        drop default;

alter table voteoption
    alter column participantlimit
        drop default;
