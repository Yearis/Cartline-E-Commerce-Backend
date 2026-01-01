USE cartline_db;

DROP TABLE IF EXISTS order_items;
DROP TABLE IF EXISTS orders;
DROP TABLE IF EXISTS cart_items;
DROP TABLE IF EXISTS carts;
DROP TABLE IF EXISTS products_categories;
DROP TABLE IF EXISTS categories;
DROP TABLE IF EXISTS product_review_helpful_users;
DROP TABLE IF EXISTS product_reviews;
DROP TABLE IF EXISTS products;
DROP TABLE IF EXISTS seller_reviews;
DROP TABLE IF EXISTS sellers;
DROP TABLE IF EXISTS users_roles;
DROP TABLE IF EXISTS users;
DROP TABLE IF EXISTS roles;

INSERT INTO users (first_name, last_name, email, password) VALUES ('Khush', 'Arora', 'khush8751@gmail.com', '$2a$10$sfGFbsoRvD0N1zsTSWTJjuTG1l8aFWkRgbNURSupT7nFGIkJ5uF.G');

INSERT INTO roles (name) VALUES ('ROLE_ADMIN');
INSERT INTO roles (name) VALUES ('ROLE_SELLER');
INSERT INTO roles (name) VALUES ('ROLE_USER');

INSERT IGNORE INTO users_roles (user_id, role_id) VALUES (10001, 10001);
INSERT IGNORE INTO users_roles (user_id, role_id) VALUES (10001, 10002);
INSERT IGNORE INTO users_roles (user_id, role_id) VALUES (10001, 10003);