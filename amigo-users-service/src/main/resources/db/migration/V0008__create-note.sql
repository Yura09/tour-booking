create table if not exists note
(
    id uuid default uuid_generate_v4() not null constraint note_pkey primary key,
    created_by          uuid            not null,
    created_date        timestamptz     not null,
    last_modified_by    uuid,
    last_modified_date  timestamptz,
    deleted             boolean         default false,
    container_class     varchar(255)    not null,
    container_id        uuid            not null,
    content             text            not null
);

CREATE INDEX note_container_idx ON note(container_class, container_id) where deleted is false;