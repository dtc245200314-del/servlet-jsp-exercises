-- Import into a fresh practice database. Existing tables/data are not deleted.
CREATE DATABASE IF NOT EXISTS codegym_final CHARACTER SET utf8mb4;
USE codegym_final;
CREATE TABLE products (
    id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    price DECIMAL(15,2) NOT NULL,
    quantity INT NOT NULL,
    CONSTRAINT chk_product_price CHECK(price >= 0),
    CONSTRAINT chk_product_quantity CHECK(quantity >= 0)
) ENGINE=InnoDB;
INSERT INTO products(name,price,quantity) VALUES
('Bàn phím cơ',850000.00,12),
('Chuột không dây',320000.00,25),
('Sổ tay lập trình',65000.00,40);
