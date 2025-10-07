--liquibase formatted sql
--changeset admin:4

CREATE INDEX item_price_index ON items(price);
CREATE INDEX idx_orders_user_id ON orders(user_id);
CREATE INDEX idx_orders_status ON orders(status);
CREATE INDEX idx_order_items_order_id ON order_items(order_id);