
--changeset admin:4
INSERT INTO items (name, price) VALUES
                                    ('laptop', 999.99),
                                    ('smartphone', 699.99),
                                    ('headphones', 149.99);

-- --changeset admin:5
-- INSERT INTO orders (user_id, status, creation_date) VALUES
--                                                         (1, 'CREATED', '2024-10-01'),
--                                                         (2, 'PROCESSING', '2023-10-02'),
--                                                         (3, 'COMPLETED', '2022-10-03');
--
-- --changeset admin:6
-- INSERT INTO order_items (quantity, order_id, item_id) VALUES
--                                                           (1, 1, 1),  -- Заказ 1 содержит 1 ноутбук
--                                                           (2, 1, 3),  -- Заказ 1 содержит 2 наушника
--                                                           (1, 2, 2),  -- Заказ 2 содержит 1 смартфон
--                                                           (3, 2, 3),  -- Заказ 2 содержит 3 наушника
--                                                           (2, 3, 1),  -- Заказ 3 содержит 2 ноутбука
--                                                           (1, 3, 2);  -- Заказ 3 содержит 1 смартфон
--