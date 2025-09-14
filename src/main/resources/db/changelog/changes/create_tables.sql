--liquibase formatted sql

--changeset admin:1
CREATE TABLE items
(
    item_id SERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    price DECIMAL(255,2) NOT NULL
);
--rollback DROP TABLE items;

--changeset admin:2
CREATE TABLE orders
(
    order_id SERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    status VARCHAR(32) DEFAULT 'PENDING',
    creation_date TIMESTAMP NOT NULL DEFAULT CURRENT_DATE
);
--rollback DROP TABLE orders

--changeset admin:3
CREATE TABLE order_items
(
    order_items_id SERIAL PRIMARY KEY,
    quantity INT NOT NULL,
    order_id BIGINT NOT NULL REFERENCES orders(order_id) ON DELETE CASCADE,
    item_id BIGINT NOT NULL REFERENCES items(item_id) ON DELETE CASCADE
);
--rollback DROP TABLE order_items