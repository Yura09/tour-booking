-- Insert partners
INSERT INTO users.partner (id, name, address, zip, time_zone, business_type, contact_phones, emails, enabled, partner_group_id, country, city, website, master,
                           ta_contact_id, account_manager_id)
VALUES ('039ad870-a993-4b4d-8df9-5869ab0f1b81', 'Tour Amigo', 'Lautrec Street, Bracken Ridge, Brisbane, Queensland', '4017', 'ACST', 'AFFILIATE',
        '+61730678616,+61499280587', 'admin@touramigo.com,product@touramigo.com', true, null, null, null, null, false, null, null)
ON CONFLICT DO NOTHING;

-- Insert products
INSERT INTO users.product (id, name, description, codevariable)
VALUES ('1c925ed4-21d9-4c57-a644-d0904366131e', 'Tour Amigo Administration', 'Administration platform for controlling all the services', 'allow-admin')
ON CONFLICT DO NOTHING;
INSERT INTO users.product (id, name, description, codevariable)
VALUES ('d730ca4e-a478-4625-92e3-c5eff89e5402', 'Hermes Saas', 'Hermes tour product', 'allow-hermes')
ON CONFLICT DO NOTHING;

-- Insert partner products
INSERT INTO users.partner_product (id, partner_id, product_id)
VALUES ('32e94134-cdd9-4bed-9713-53dda52fbbc8', '039ad870-a993-4b4d-8df9-5869ab0f1b81', '1c925ed4-21d9-4c57-a644-d0904366131e')
ON CONFLICT DO NOTHING;

-- Insert permission groups
INSERT INTO users.permission_group (id, name)
VALUES ('95119f08-8509-4c81-b5e6-8845a9c856cb', 'OTHER')
ON CONFLICT DO NOTHING;
INSERT INTO users.permission_group (id, name)
VALUES ('bb12d3a6-3a10-4e83-9584-b4c649edc82b', 'Administration')
ON CONFLICT DO NOTHING;
INSERT INTO users.permission_group (id, name)
VALUES ('1791b89b-2c49-4a2b-bac4-ffecd4be2666', 'Partners')
ON CONFLICT DO NOTHING;

-- Insert permissions
INSERT INTO users.permission (id, active, name, description, codekey, product_id, group_id, master)
VALUES ('9f268df4-b324-49b2-956e-ecb5cab5948d', true, 'Strategic Partners Administration', 'This will restrict global strategic partner administration',
        'administrate-partners', '1c925ed4-21d9-4c57-a644-d0904366131e', 'bb12d3a6-3a10-4e83-9584-b4c649edc82b', true)
ON CONFLICT DO NOTHING;
INSERT INTO users.permission (id, active, name, description, codekey, product_id, group_id, master)
VALUES ('fc6f89d9-b27b-454f-b14a-a5a22d0fb2ad', true, 'Suppliers Administration', 'This will restrict global suppliers administration',
        'administrate-suppliers', '1c925ed4-21d9-4c57-a644-d0904366131e', 'bb12d3a6-3a10-4e83-9584-b4c649edc82b', true)
ON CONFLICT DO NOTHING;
INSERT INTO users.permission (id, active, name, description, codekey, product_id, group_id, master)
VALUES ('626a85d5-7d08-4386-891b-71838b1e30cb', true, 'Bookings Administration', 'This will restrict global bookings administration', 'administrate-bookings',
        '1c925ed4-21d9-4c57-a644-d0904366131e', 'bb12d3a6-3a10-4e83-9584-b4c649edc82b', true)
ON CONFLICT DO NOTHING;
INSERT INTO users.permission (id, active, name, description, codekey, product_id, group_id, master)
VALUES ('015c3a6d-33f1-4098-a93a-6af3e4baa579', true, 'Global Configuration', 'This will restrict access to global system configuration', 'administrate-global',
        '1c925ed4-21d9-4c57-a644-d0904366131e', 'bb12d3a6-3a10-4e83-9584-b4c649edc82b', true)
ON CONFLICT DO NOTHING;

-- Insert users
INSERT INTO users."user" (id, password, email, enabled, full_name, job_title, contact_number, image_id, partner_id, user_name, supplier_id, enabled_before)
VALUES ('fdec894c-a1ca-41e6-b547-8f54f3d0606c', '$2a$10$Q1KmVt3bRbnbwmRh94wnAe4qTm7aoevQ5yT/GFUa/YmjCvHfWkzPi', 'admin@touramigo.com', true, 'Amigo Admin',
        'Tour Amigo Admin', '123123123', null, '039ad870-a993-4b4d-8df9-5869ab0f1b81', 'Amigo Admin', null, true)
ON CONFLICT DO NOTHING;

-- Insert partner product permissions
INSERT INTO users.pp_permission (id, pp_id, permission_id)
VALUES ('7d50ed9f-5360-4ec1-8517-b3f99c3b11b5', '32e94134-cdd9-4bed-9713-53dda52fbbc8', '9f268df4-b324-49b2-956e-ecb5cab5948d')
ON CONFLICT DO NOTHING;
INSERT INTO users.pp_permission (id, pp_id, permission_id)
VALUES ('e9d8fa29-9b90-43f2-9cca-6d68f064f4fd', '32e94134-cdd9-4bed-9713-53dda52fbbc8', 'fc6f89d9-b27b-454f-b14a-a5a22d0fb2ad')
ON CONFLICT DO NOTHING;
INSERT INTO users.pp_permission (id, pp_id, permission_id)
VALUES ('229a88c7-ccef-4781-b023-40dec6a45ad6', '32e94134-cdd9-4bed-9713-53dda52fbbc8', '626a85d5-7d08-4386-891b-71838b1e30cb')
ON CONFLICT DO NOTHING;
INSERT INTO users.pp_permission (id, pp_id, permission_id)
VALUES ('a1f61c9b-2408-47b8-a5f1-15dbcfee8fb4', '32e94134-cdd9-4bed-9713-53dda52fbbc8', '015c3a6d-33f1-4098-a93a-6af3e4baa579')
ON CONFLICT DO NOTHING;

-- Insert user products
INSERT INTO users.user_product (id, user_id, product_id)
VALUES ('6941ae1e-51e4-476d-9a19-ea9ea3d77de6', 'fdec894c-a1ca-41e6-b547-8f54f3d0606c', '1c925ed4-21d9-4c57-a644-d0904366131e')
ON CONFLICT DO NOTHING;

-- Insert user product permissions
INSERT INTO users.up_permission (id, up_id, permission_id)
VALUES ('af86fa76-b3e4-4f9e-a1d1-d768a4eaa039', '6941ae1e-51e4-476d-9a19-ea9ea3d77de6', '9f268df4-b324-49b2-956e-ecb5cab5948d')
ON CONFLICT DO NOTHING;
INSERT INTO users.up_permission (id, up_id, permission_id)
VALUES ('6133fa86-2021-4664-a9fa-4e404d0f2e12', '6941ae1e-51e4-476d-9a19-ea9ea3d77de6', 'fc6f89d9-b27b-454f-b14a-a5a22d0fb2ad')
ON CONFLICT DO NOTHING;
INSERT INTO users.up_permission (id, up_id, permission_id)
VALUES ('a058593c-f1b6-4ade-9cd7-49ba0769465d', '6941ae1e-51e4-476d-9a19-ea9ea3d77de6', '626a85d5-7d08-4386-891b-71838b1e30cb')
ON CONFLICT DO NOTHING;
INSERT INTO users.up_permission (id, up_id, permission_id)
VALUES ('4af20d68-f60d-4683-9667-305935cc991c', '6941ae1e-51e4-476d-9a19-ea9ea3d77de6', '015c3a6d-33f1-4098-a93a-6af3e4baa579')
ON CONFLICT DO NOTHING;