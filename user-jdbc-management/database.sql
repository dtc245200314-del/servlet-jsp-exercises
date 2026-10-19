-- Run in a NEW dedicated practice schema. No existing tables are dropped.
CREATE DATABASE IF NOT EXISTS codegym_java CHARACTER SET utf8mb4;
USE codegym_java;
CREATE TABLE users(id INT PRIMARY KEY AUTO_INCREMENT,name VARCHAR(50) NOT NULL,email VARCHAR(100) NOT NULL UNIQUE,country VARCHAR(50) NOT NULL);
CREATE TABLE permission(id INT PRIMARY KEY,name VARCHAR(50) NOT NULL);
INSERT INTO permission VALUES(1,'add'),(2,'edit'),(3,'delete'),(4,'view');
CREATE TABLE user_permission(user_id INT NOT NULL,permission_id INT NOT NULL,PRIMARY KEY(user_id,permission_id),FOREIGN KEY(user_id) REFERENCES users(id) ON DELETE CASCADE,FOREIGN KEY(permission_id) REFERENCES permission(id));
CREATE TABLE Employee(id INT PRIMARY KEY AUTO_INCREMENT,name VARCHAR(100) NOT NULL,salary DECIMAL(15,2) NOT NULL,created_Date TIMESTAMP NOT NULL);
INSERT INTO users(name,email,country) VALUES('Nguyen An','an@example.com','Viet Nam'),('Bao Tran','bao@example.com','Singapore'),('Chi Le','chi@example.com','Viet Nam');
DELIMITER $$
CREATE PROCEDURE get_user_by_id(IN user_id INT)
BEGIN SELECT name,email,country FROM users WHERE id=user_id; END$$
CREATE PROCEDURE insert_user(IN user_name VARCHAR(50),IN user_email VARCHAR(100),IN user_country VARCHAR(50))
BEGIN INSERT INTO users(name,email,country) VALUES(user_name,user_email,user_country); END$$
CREATE PROCEDURE select_all_users(IN country_filter VARCHAR(50),IN sort_name BOOLEAN)
BEGIN
 SELECT id,name,email,country FROM users
 WHERE country_filter='' OR country LIKE CONCAT('%',REPLACE(REPLACE(REPLACE(country_filter,'!','!!'),'%','!%'),'_','!_'),'%') ESCAPE '!'
 ORDER BY CASE WHEN sort_name THEN name END,id;
END$$
CREATE PROCEDURE update_user(IN user_id INT,IN user_name VARCHAR(50),IN user_email VARCHAR(100),IN user_country VARCHAR(50))
BEGIN UPDATE users SET name=user_name,email=user_email,country=user_country WHERE id=user_id; END$$
CREATE PROCEDURE delete_user(IN user_id INT)
BEGIN DELETE FROM users WHERE id=user_id; END$$
DELIMITER ;
