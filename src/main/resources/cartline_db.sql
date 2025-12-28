USE cartline_db;

CREATE TABLE IF NOT EXISTS users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    first_name VARCHAR(255) NOT NULL,
    last_name VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL
) AUTO_INCREMENT = 10001;

CREATE TABLE IF NOT EXISTS roles (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(60) NOT NULL UNIQUE
) AUTO_INCREMENT = 10001;

CREATE TABLE IF NOT EXISTS users_roles (
    user_id BIGINT NOT NULL,
    role_id BIGINT NOT NULL,

    PRIMARY KEY (user_id, role_id),

    FOREIGN KEY (user_id)
    REFERENCES users(id)
    ON DELETE CASCADE,

    FOREIGN KEY (role_id)
    REFERENCES roles(id)
    ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS sellers (
	user_id BIGINT NOT NULL PRIMARY KEY,
    store_name VARCHAR(255) NOT NULL UNIQUE,
    business_phone_number VARCHAR(255) UNIQUE,
    seller_status VARCHAR(50),
    
    business_address_line1 VARCHAR(255) NOT NULL UNIQUE,
    business_address_line2 VARCHAR(255),
    business_landmark VARCHAR(255),
    business_city VARCHAR(255) NOT NULL,
    business_state VARCHAR(255) NOT NULL,
    business_country VARCHAR(255) NOT NULL,
    business_zip_code VARCHAR(255) NOT NULL,
    
    FOREIGN KEY (user_id)
    REFERENCES users(id)
    ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS seller_reviews (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    overall_rating DOUBLE,
    delivery_rating INT,
    product_accuracy_rating INT,
    service_rating INT,
    packaging_rating INT,
    user_id BIGINT NOT NULL,
    seller_id BIGINT NOT NULL,

    UNIQUE KEY unique_user_seller_rev (user_id, seller_id),

    FOREIGN KEY (user_id)
    REFERENCES users(id)
    ON DELETE CASCADE,

    FOREIGN KEY (seller_id)
    REFERENCES sellers(user_id)
    ON DELETE CASCADE
) AUTO_INCREMENT = 10001;

CREATE TABLE IF NOT EXISTS products (
	id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name TEXT NOT NULL,
    brand VARCHAR(255) NOT NULL,
    description TEXT NOT NULL,
    price DECIMAL(13, 2) NOT NULL,
    discount DECIMAL(13, 2), -- This can be null lol
    inventory INT UNSIGNED,
    version BIGINT DEFAULT 0,
    status VARCHAR(50) DEFAULT 'ACTIVE',
    seller_id BIGINT,
    
    FOREIGN KEY (seller_id)
    REFERENCES sellers(user_id)
)AUTO_INCREMENT = 10001;

CREATE TABLE IF NOT EXISTS product_reviews (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    rating INT,
    comment TEXT,
    helpful_counter BIGINT DEFAULT 0,
    verified_purchase BIT(1) DEFAULT 0,

    user_id BIGINT NOT NULL,
    product_id BIGINT NOT NULL,

    UNIQUE KEY unique_user_prod_rev (user_id, product_id),

    FOREIGN KEY (user_id)
    REFERENCES users(id)
    ON DELETE CASCADE,

    FOREIGN KEY (product_id)
    REFERENCES products(id)
    ON DELETE CASCADE
) AUTO_INCREMENT = 10001;

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
    total_amount DECIMAL(13, 2) NOT NULL DEFAULT 0.00,
    user_id BIGINT NOT NULL,

    FOREIGN KEY (user_id)
    REFERENCES users(id)
    ON DELETE CASCADE
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

CREATE TABLE IF NOT EXISTS orders (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    order_date_time DATETIME NOT NULL,
    total_amount DECIMAL(13, 2) NOT NULL,
    order_status VARCHAR(255) NOT NULL,

    shipping_address_line1 VARCHAR(255) NOT NULL,
    shipping_address_line2 VARCHAR(255) NOT NULL,
    shipping_landmark VARCHAR(255) NOT NULL,
    shipping_city VARCHAR(255) NOT NULL,
    shipping_state VARCHAR(255) NOT NULL,
    shipping_country VARCHAR(255) NOT NULL,
    shipping_zip_code VARCHAR(255) NOT NULL,
    user_id BIGINT NOT NULL,

    FOREIGN KEY (user_id)
    REFERENCES users(id)
    ON DELETE CASCADE
)AUTO_INCREMENT = 10001;

CREATE TABLE IF NOT EXISTS order_items (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    quantity INT NOT NULL,
    unit_price DECIMAL(13, 2) NOT NULL,
    total_price DECIMAL(13, 2) NOT NULL,
    product_id BIGINT NOT NULL,
    order_id BIGINT NOT NULL,

    FOREIGN KEY (product_id)
    REFERENCES products(id),

    FOREIGN KEY (order_id)
    REFERENCES orders(id)
    ON DELETE CASCADE
)AUTO_INCREMENT = 10001;

INSERT INTO roles (name) VALUES ('ROLE_USER');
INSERT INTO roles (name) VALUES ('ROLE_ADMIN');

INSERT IGNORE INTO users_roles (user_id, role_id) VALUES (10001, 10002);
