USE cartline_db;

CREATE TABLE IF NOT EXISTS products (
	id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name TEXT NOT NULL,
    brand VARCHAR(255) NOT NULL,
    description TEXT NOT NULL,
    price DECIMAL(13, 2) NOT NULL,
    discount DECIMAL(13, 2), -- This can be null lol
    inventory INT UNSIGNED    
)AUTO_INCREMENT = 10001;

CREATE TABLE IF NOT EXISTS categories(
	id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL
)AUTO_INCREMENT = 10001;

CREATE TABLE IF NOT EXISTS products_categories(
	product_id BIGINT NOT NULL,
    category_id BIGINT NOT NULL,
    
    PRIMARY KEY (product_id, category_id),

	FOREIGN KEY (product_id)
    REFERENCES products(id)
    ON DELETE CASCADE,
    
    FOREIGN KEY (category_id)
    REFERENCES categories(id)
    ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS carts (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    total_amount DECIMAL(13, 2) NOT NULL
#     user_id BIGINT NOT NULL,
#
#     FOREIGN KEY (user_id)
#     REFERENCES users(id)
#     ON DELETE CASCADE
)AUTO_INCREMENT = 10001;

CREATE TABLE IF NOT EXISTS cart_items (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    quantity INT NOT NULL,
    unit_price DECIMAL(13, 2) NOT NULL,
    total_price DECIMAL(13, 2) NOT NULL,
    product_id BIGINT NOT NULL,
    cart_id BIGINT NOT NULL,

    FOREIGN KEY (product_id)
    REFERENCES products(id),

    FOREIGN KEY (cart_id)
    REFERENCES carts(id)
    ON DELETE CASCADE
)AUTO_INCREMENT = 10001;