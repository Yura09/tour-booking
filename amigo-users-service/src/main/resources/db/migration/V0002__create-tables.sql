create table if not exists image
(
    id        uuid default uuid_generate_v4() not null
        constraint image_pkey_
            primary key,
    image_url text                            not null,
    cloud_url text,
    width     integer,
    height    integer
);

create table if not exists "user"
(
    id                      uuid    default uuid_generate_v4() not null
        constraint user_pkey_
            primary key,
    password                text                               not null,
    email                   text                               not null
        constraint unique_email_
            unique,
    enabled                 boolean default false              not null,
    enabled_before          boolean default false,
    full_name               text                               not null,
    job_title               text,
    contact_number          text,
    image_id                uuid
        constraint user_image_id_fkey_
            references image,
    partner_id              uuid                               not null,
    user_name               text                               not null,
    supplier_id             uuid,
    credentials_not_expired boolean default true
);

create table if not exists partner_group
(
    id   uuid default uuid_generate_v4() not null
        constraint company_group_pkey_
            primary key,
    name text                            not null
);

create table if not exists partner
(
    id                 uuid    default uuid_generate_v4() not null
        constraint company_pkey_
            primary key,
    name               text                               not null,
    address            text                               not null,
    zip                text                               not null,
    time_zone          text,
    business_type      text,
    contact_phones     text,
    emails             text,
    enabled            boolean default true               not null,
    partner_group_id   uuid
        constraint company_group_id_fkey_
            references partner_group,
    country            text,
    city               text,
    website            text,
    master             boolean default false              not null,
    ta_contact_id      uuid
        constraint fk_partner_user_
            references "user",
    account_manager_id uuid
        constraint fk_account_manager_partner_
            references users."user"
);

alter table "user"
    add constraint fk_users_company_
        foreign key (partner_id) references partner;

create table if not exists temporal_link
(
    id         uuid default uuid_generate_v4() not null
        constraint temporal_link_pkey_
            primary key,
    token      text                            not null,
    expired_at timestamp                       not null,
    user_id    uuid                            not null
        constraint temporal_link_user_id_fkey_
            references "user",
    type       text
);

create table if not exists supplier
(
    id                 uuid    default uuid_generate_v4() not null
        constraint supplier_pkey_
            primary key,
    name               text                               not null,
    address            text,
    zip                text,
    contact_phones     text,
    emails             text,
    country            text,
    city               text,
    enabled            boolean default true               not null,
    website_url        text,
    supplier_type      text,
    partner_id         uuid
        constraint fk_supplier_partner_
            references partner,
    code_variable      text,
    account_manager_id uuid
        constraint fk_account_manager_supplier_
            references users."user"
);

alter table "user"
    add constraint user_suppliers_id_fkey_
        foreign key (supplier_id) references supplier;

create table if not exists supplier_connection
(
    supplier_id uuid                            not null
        constraint supplier_supplier_id_fkey_
            references supplier,
    key         text                            not null,
    id          uuid default uuid_generate_v4() not null
        constraint supplier_connection_pkey_
            primary key
);

create table if not exists partner_supplier
(
    partner_id  uuid not null
        constraint partner_supplier_partner_id_fkey_
            references partner,
    supplier_id uuid not null
        constraint partner_supplier_supplier_id_fkey_
            references supplier,
    constraint partner_supplier_pkey_
        primary key (partner_id, supplier_id)
);

create table if not exists product
(
    id           uuid default uuid_generate_v4() not null
        constraint global_product_pkey_
            primary key,
    name         text                            not null,
    description  text,
    codevariable text                            not null
);

create table if not exists partner_product
(
    id         uuid default uuid_generate_v4() not null
        constraint id_unique_
            unique,
    partner_id uuid                            not null
        constraint fk_pp_partner_
            references partner
            on delete cascade,
    product_id uuid                            not null
        constraint fk_pp_product_
            references product
            on delete cascade,
    constraint partner_product_pkey_
        primary key (partner_id, product_id)
);

create table if not exists permission_group
(
    id   uuid default uuid_generate_v4() not null
        constraint permission_group_pkey_
            primary key,
    name text                            not null
);

create table if not exists permission
(
    id          uuid    default uuid_generate_v4() not null
        constraint global_permission_pkey_
            primary key,
    active      boolean                            not null,
    name        text                               not null,
    description text,
    codekey     text                               not null,
    product_id  uuid
        constraint fk_permission_product_
            references product
            on delete cascade,
    group_id    uuid                               not null
        constraint permission_group_id_fkey_
            references permission_group,
    master      boolean default false              not null
);

create table if not exists pp_permission
(
    id            uuid default uuid_generate_v4() not null
        constraint pp_permission_pkey_
            primary key,
    pp_id         uuid
        constraint fk_pp_id_
            references partner_product (id)
            on delete cascade,
    permission_id uuid
        constraint fk_pp_permission_id_
            references permission
            on delete cascade
);

create table if not exists connection_value
(
    id                     uuid default uuid_generate_v4() not null
        constraint connection_value_pkey_
            primary key,
    value                  text,
    partner_id             uuid
        constraint connection_value_partner_id_fkey_
            references partner,
    supplier_connection_id uuid
        constraint connection_value_supplier_connection_id_fkey_
            references supplier_connection
);

create table if not exists user_product
(
    id         uuid default uuid_generate_v4() not null
        constraint user_product_pkey_
            primary key,
    user_id    uuid
        constraint fk_pp_partner_
            references "user",
    product_id uuid
        constraint fk_pp_product_
            references product
);

create table if not exists up_permission
(
    id            uuid default uuid_generate_v4() not null
        constraint up_permission_pkey_
            primary key,
    up_id         uuid
        constraint fk_up_id_
            references user_product,
    permission_id uuid
        constraint fk_pp_permission_id_
            references permission
);

create table if not exists operational_group
(
    id         uuid default uuid_generate_v4() not null
        constraint operational_group_pkey_
            primary key,
    name       text                            not null,
    partner_id uuid
        constraint fk_op_partner_
            references partner
);

create table if not exists user_operational
(
    id             uuid default uuid_generate_v4() not null
        constraint user_operational_pkey_
            primary key,
    user_id        uuid
        constraint fk_ogroup_userid_
            references "user",
    operational_id uuid
        constraint fk_ogroup_ogroupid_
            references operational_group
);

create table if not exists terms_and_conditions
(
    id           uuid      not null
        constraint terms_and_conditions_pk_
            primary key,
    supplier_id  uuid      not null,
    version      integer   not null,
    created_at   timestamp not null,
    created_by   uuid      not null,
    published_at timestamp,
    published_by uuid,
    state        varchar(32),
    type         varchar(32),
    description  text,
    content      text,
    constraint terms_and_conditions_uk_2_
        unique (supplier_id, version)
);

create table if not exists users.permission_operational
(
    id             uuid default uuid_generate_v4() primary key,
    permission_id  uuid,
    operational_id uuid,
    constraint fk_ogroup_permissionid foreign key (permission_id) references users.permission (id),
    constraint fk_ogroup_ogroupid foreign key (operational_id) references users.operational_group (id)
);