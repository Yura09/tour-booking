create table partner_history
(
    id          uuid default uuid_generate_v4() not null
        constraint partner_history_pkey_ primary key,
    modified_by uuid                            not null,
    partner_id  uuid                            not null,
    date_log    timestamptz                     not null,
    data        jsonb                           not null,
    constraint fk_partner_history_partner_ foreign key (partner_id) references users.partner (id) on delete cascade
);
