TRUNCATE TABLE order_item, orders, delivery_slot, product, users RESTART IDENTITY CASCADE;

INSERT INTO users (creation_date, deleted, update_time, updater_id, email, first_name, last_name, password, role)
VALUES (now(), false, now(), 0, 'admin@freshcart.test', 'Ada', 'Admin', '$2a$10$7xQtnqZvGdiqlukbVnIiYeH9gyfqV9xTDN91V6YqxY5q744e9X2xi', 'ADMIN'),
       (now(), false, now(), 0, 'customer1@freshcart.test', 'Cas', 'One', '$2a$10$7xQtnqZvGdiqlukbVnIiYeH9gyfqV9xTDN91V6YqxY5q744e9X2xi', 'CUSTOMER'),
       (now(), false, now(), 0, 'customer2@freshcart.test', 'Cor', 'Two', '$2a$10$7xQtnqZvGdiqlukbVnIiYeH9gyfqV9xTDN91V6YqxY5q744e9X2xi', 'CUSTOMER');

INSERT INTO product (creation_date, deleted, update_time, updater_id, name, price, stock, version)
VALUES (now(), false, now(), 0, 'Milk', 1.20, 1000, 0),
       (now(), false, now(), 0, 'Bread', 2.50, 1000, 0),
       (now(), false, now(), 0, 'Saffron', 9.99, 2, 0);

INSERT INTO delivery_slot (creation_date, deleted, update_time, updater_id, start_time, end_time, max_order)
VALUES (now(), false, now(), 0, now() + interval '1 day', now() + interval '1 day 2 hours', 100),
       (now(), false, now(), 0, now() + interval '2 days', now() + interval '2 days 2 hours', 1),
       (now(), false, now(), 0, now() - interval '1 hour', now() + interval '1 hour', 100),
       (now(), false, now(), 0, now() + interval '3 days', now() + interval '3 days 2 hours', 5);

INSERT INTO orders (creation_date, deleted, update_time, updater_id, order_status, total_price, version, slot_id, user_id)
VALUES (now(), false, now(), 0, 'ORDER_APPROVED', 1.20, 0, 2, 2);

INSERT INTO order_item (creation_date, deleted, update_time, updater_id, price, quantity, order_id, product_id)
VALUES (now(), false, now(), 0, 1.20, 1, 1, 1);
