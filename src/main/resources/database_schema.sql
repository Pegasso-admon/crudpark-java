-- CrudPark Database Schema for MySQL
-- Run this script to create all required tables

-- Create database if not exists
CREATE DATABASE IF NOT EXISTS crudpark;
USE crudpark;

-- Operators table
CREATE TABLE IF NOT EXISTS operators (
    id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(100),
    active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- Memberships table
CREATE TABLE IF NOT EXISTS memberships (
    id INT AUTO_INCREMENT PRIMARY KEY,
    owner_name VARCHAR(100) NOT NULL,
    owner_email VARCHAR(100),
    plate VARCHAR(20) NOT NULL,
    start_date DATE NOT NULL,
    end_date DATE NOT NULL,
    active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_plate (plate),
    INDEX idx_dates (start_date, end_date)
);

-- Rates table
CREATE TABLE IF NOT EXISTS rates (
    id INT AUTO_INCREMENT PRIMARY KEY,
    base_rate DECIMAL(10, 2) NOT NULL,
    fraction_rate DECIMAL(10, 2) NOT NULL,
    daily_cap DECIMAL(10, 2),
    grace_minutes INT DEFAULT 30,
    active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- Tickets table
CREATE TABLE IF NOT EXISTS tickets (
    id INT AUTO_INCREMENT PRIMARY KEY,
    plate VARCHAR(20) NOT NULL,
    entry_time TIMESTAMP NOT NULL,
    exit_time TIMESTAMP NULL,
    ticket_type ENUM('MONTHLY', 'GUEST') NOT NULL,
    operator_id INT NOT NULL,
    active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (operator_id) REFERENCES operators(id),
    INDEX idx_plate (plate),
    INDEX idx_active (active),
    INDEX idx_entry_time (entry_time)
);

-- Payments table
CREATE TABLE IF NOT EXISTS payments (
    id INT AUTO_INCREMENT PRIMARY KEY,
    ticket_id INT NOT NULL,
    amount DECIMAL(10, 2) NOT NULL,
    payment_method ENUM('CASH', 'CARD', 'TRANSFER') NOT NULL,
    payment_time TIMESTAMP NOT NULL,
    operator_id INT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (ticket_id) REFERENCES tickets(id),
    FOREIGN KEY (operator_id) REFERENCES operators(id),
    INDEX idx_ticket_id (ticket_id),
    INDEX idx_payment_time (payment_time)
);

-- Insert default operator (username: admin, password: admin123)
INSERT INTO operators (username, password_hash, name, email, active) 
VALUES ('admin', 'admin123', 'Administrator', 'admin@crudpark.com', TRUE)
ON DUPLICATE KEY UPDATE username=username;

-- Insert default rate
INSERT INTO rates (base_rate, fraction_rate, daily_cap, grace_minutes, active)
VALUES (5000.00, 3000.00, 50000.00, 30, TRUE)
ON DUPLICATE KEY UPDATE id=id;

-- Insert test membership (valid for 30 days from today)
INSERT INTO memberships (owner_name, owner_email, plate, start_date, end_date, active)
VALUES ('John Doe', 'john@example.com', 'ABC123', CURDATE(), DATE_ADD(CURDATE(), INTERVAL 30 DAY), TRUE)
ON DUPLICATE KEY UPDATE owner_name=owner_name;