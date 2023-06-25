create table if not exists contact
(
    id           uuid default uuid_generate_v4() not null
        constraint contact_pkey_
            primary key,
    name         text                            not null,
    job_title    text,
    email        text,
    mobile       text,
    telephone    text,
    linkedin text,
    social_media text,
    supplier_id  uuid,
    constraint fk_contact_supplier_ foreign key (supplier_id) references users.supplier (id)
);