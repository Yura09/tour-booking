INSERT INTO users.permission (id, active, name, description, codekey, product_id, group_id, master) VALUES ('c7ce2d2a-41f2-4d42-a8d7-7dc26c4ee423', true, 'Users Management', 'This will restrict partner user management2', 'manage-users', '1c925ed4-21d9-4c57-a644-d0904366131e', '1791b89b-2c49-4a2b-bac4-ffecd4be2666', false)  ON CONFLICT DO NOTHING;
INSERT INTO users.permission (id, active, name, description, codekey, product_id, group_id, master) VALUES ('a3a808bf-4ed4-44bc-954a-7c16d6e908b8', true, 'Suppliers Management', 'This will restrict partner supplier management', 'manage-supplier', '1c925ed4-21d9-4c57-a644-d0904366131e', '1791b89b-2c49-4a2b-bac4-ffecd4be2666', false)  ON CONFLICT DO NOTHING;
INSERT INTO users.permission (id, active, name, description, codekey, product_id, group_id, master) VALUES ('da464d35-3dfd-4478-b297-9ce062efa27a', true, 'Booking Management', 'This will restrict partner bookings management', 'manage-bookings', '1c925ed4-21d9-4c57-a644-d0904366131e', '1791b89b-2c49-4a2b-bac4-ffecd4be2666', false)  ON CONFLICT DO NOTHING;