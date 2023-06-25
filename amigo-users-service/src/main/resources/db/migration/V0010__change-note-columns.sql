drop index if exists note_container_idx;

alter table users.note
    drop column container_class,
    drop column container_id,
    add column supplier_id uuid,
    add column partner_id uuid,
    add column contact_id uuid,
    add column rule_id uuid,
    add constraint fk_note_supplier_ foreign key (supplier_id) references users.supplier (id) on delete cascade,
    add constraint fk_note_partner_ foreign key (partner_id) references users.partner (id) on delete cascade,
    add constraint fk_note_contact_ foreign key (contact_id) references users.contact (id) on delete cascade,
    add constraint fk_note_rule_ foreign key (rule_id) references commercial.rule (id) on delete cascade