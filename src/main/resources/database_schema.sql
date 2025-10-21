-- CrudPark Database Schema for PostgreSQL
-- Run this script to create all required tables

-- Drop tables if they exist (for clean installation)
DROP TABLE IF EXISTS payments CASCADE;
DROP TABLE IF EXISTS tickets CASCADE;
DROP TABLE IF EXISTS memberships CASCADE;
DROP TABLE IF EXISTS rates CASCADE;
DROP TABLE IF EXISTS operators CASCADE;

-- Drop types if they exist
DROP TYPE IF EXISTS ticket_type_enum CASCADE;
DROP TYPE IF EXISTS payment_method_enum CASCADE;

-- Create custom ENUM types (BEFORE creating tables)
CREATE TYPE ticket_type_enum AS ENUM ('MONTHLY', 'GUEST');
CREATE TYPE payment_method_enum AS ENUM ('CASH', 'CARD', 'TRANSFER');

-- Operators table
CREATE TABLE operators (
    id SERIAL PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(100),
    active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Create trigger function for updated_at
CREATE OR REPLACE FUNCTION update_updated_at_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ language 'plpgsql';

-- Create trigger for operators
CREATE TRIGGER update_operators_updated_at BEFORE UPDATE ON operators
FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

-- Memberships table
CREATE TABLE memberships (
    id SERIAL PRIMARY KEY,
    owner_name VARCHAR(100) NOT NULL,
    owner_email VARCHAR(100),
    plate VARCHAR(20) NOT NULL,
    start_date DATE NOT NULL,
    end_date DATE NOT NULL,
    active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_memberships_plate ON memberships(plate);
CREATE INDEX idx_memberships_dates ON memberships(start_date, end_date);

-- Create trigger for memberships
CREATE TRIGGER update_memberships_updated_at BEFORE UPDATE ON memberships
FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

-- Rates table
CREATE TABLE rates (
    id SERIAL PRIMARY KEY,
    base_rate NUMERIC(10, 2) NOT NULL,
    fraction_rate NUMERIC(10, 2) NOT NULL,
    daily_cap NUMERIC(10, 2),
    grace_minutes INTEGER DEFAULT 30,
    active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Create trigger for rates
CREATE TRIGGER update_rates_updated_at BEFORE UPDATE ON rates
FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

-- Tickets table
CREATE TABLE tickets (
    id SERIAL PRIMARY KEY,
    plate VARCHAR(20) NOT NULL,
    entry_time TIMESTAMP NOT NULL,
    exit_time TIMESTAMP NULL,
    ticket_type ticket_type_enum NOT NULL,
    operator_id INTEGER NOT NULL,
    active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (operator_id) REFERENCES operators(id) ON DELETE RESTRICT
);

CREATE INDEX idx_tickets_plate ON tickets(plate);
CREATE INDEX idx_tickets_active ON tickets(active);
CREATE INDEX idx_tickets_entry_time ON tickets(entry_time);
CREATE INDEX idx_tickets_operator_id ON tickets(operator_id);

-- Payments table
CREATE TABLE payments (
    id SERIAL PRIMARY KEY,
    ticket_id INTEGER NOT NULL,
    amount NUMERIC(10, 2) NOT NULL,
    payment_method payment_method_enum NOT NULL,
    payment_time TIMESTAMP NOT NULL,
    operator_id INTEGER NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (ticket_id) REFERENCES tickets(id) ON DELETE CASCADE,
    FOREIGN KEY (operator_id) REFERENCES operators(id) ON DELETE RESTRICT
);

CREATE INDEX idx_payments_ticket_id ON payments(ticket_id);
CREATE INDEX idx_payments_payment_time ON payments(payment_time);
CREATE INDEX idx_payments_operator_id ON payments(operator_id);

-- Insert default operator (username: admin, password: admin123)
INSERT INTO operators (username, password_hash, name, email, active) 
VALUES ('admin', 'admin123', 'Administrator', 'admin@crudpark.com', TRUE)
ON CONFLICT (username) DO NOTHING;

-- Insert default rate
INSERT INTO rates (base_rate, fraction_rate, daily_cap, grace_minutes, active)
VALUES (5000.00, 3000.00, 50000.00, 30, TRUE);

-- Insert test membership (valid for 30 days from today)
INSERT INTO memberships (owner_name, owner_email, plate, start_date, end_date, active)
VALUES ('John Doe', 'john@example.com', 'ABC123', CURRENT_DATE, CURRENT_DATE + INTERVAL '30 days', TRUE);

-- Insert additional test data
INSERT INTO operators (username, password_hash, name, email, active) 
VALUES 
    ('operator1', 'pass123', 'Maria Garcia', 'maria@crudpark.com', TRUE),
    ('operator2', 'pass123', 'Carlos Lopez', 'carlos@crudpark.com', TRUE)
ON CONFLICT (username) DO NOTHING;

-- Insert additional test memberships
INSERT INTO memberships (owner_name, owner_email, plate, start_date, end_date, active)
VALUES 
    ('Jane Smith', 'jane@example.com', 'XYZ789', CURRENT_DATE, CURRENT_DATE + INTERVAL '30 days', TRUE),
    ('Bob Johnson', 'bob@example.com', 'DEF456', CURRENT_DATE - INTERVAL '5 days', CURRENT_DATE + INTERVAL '25 days', TRUE);

-- Create useful views for reporting

-- Active tickets view
CREATE OR REPLACE VIEW v_active_tickets AS
SELECT 
    t.id,
    t.plate,
    t.entry_time,
    CAST(t.ticket_type AS VARCHAR) as ticket_type,
    o.name as operator_name,
    EXTRACT(EPOCH FROM (CURRENT_TIMESTAMP - t.entry_time))/60 as minutes_elapsed
FROM tickets t
JOIN operators o ON t.operator_id = o.id
WHERE t.active = TRUE
ORDER BY t.entry_time DESC;

-- Daily revenue view (CORREGIDO - Cast de ENUMs en CASE)
CREATE OR REPLACE VIEW v_daily_revenue AS
SELECT 
    DATE(p.payment_time) as payment_date,
    COUNT(*) as total_payments,
    SUM(p.amount) as total_revenue,
    AVG(p.amount) as avg_payment,
    COUNT(CASE WHEN CAST(p.payment_method AS VARCHAR) = 'CASH' THEN 1 END) as cash_payments,
    COUNT(CASE WHEN CAST(p.payment_method AS VARCHAR) = 'CARD' THEN 1 END) as card_payments,
    COUNT(CASE WHEN CAST(p.payment_method AS VARCHAR) = 'TRANSFER' THEN 1 END) as transfer_payments
FROM payments p
GROUP BY DATE(p.payment_time)
ORDER BY payment_date DESC;

-- Active memberships view
CREATE OR REPLACE VIEW v_active_memberships AS
SELECT 
    m.id,
    m.owner_name,
    m.plate,
    m.start_date,
    m.end_date,
    m.end_date - CURRENT_DATE as days_remaining
FROM memberships m
WHERE m.active = TRUE
AND CURRENT_DATE BETWEEN m.start_date AND m.end_date
ORDER BY m.end_date ASC;

-- Expiring memberships view (within 7 days)
CREATE OR REPLACE VIEW v_expiring_memberships AS
SELECT 
    m.id,
    m.owner_name,
    m.owner_email,
    m.plate,
    m.end_date,
    m.end_date - CURRENT_DATE as days_remaining
FROM memberships m
WHERE m.active = TRUE
AND CURRENT_DATE BETWEEN m.start_date AND m.end_date
AND m.end_date - CURRENT_DATE <= 7
ORDER BY m.end_date ASC;

-- Comments for documentation
COMMENT ON TABLE operators IS 'System users who operate the parking system';
COMMENT ON TABLE memberships IS 'Monthly parking passes for regular customers';
COMMENT ON TABLE tickets IS 'Entry/exit records for all vehicles';
COMMENT ON TABLE payments IS 'Payment transactions for guest vehicles';
COMMENT ON TABLE rates IS 'Pricing configuration for parking fees';

COMMENT ON COLUMN tickets.ticket_type IS 'MONTHLY: has valid membership, GUEST: temporary parking';
COMMENT ON COLUMN rates.grace_minutes IS 'Free parking duration before charges apply';
COMMENT ON COLUMN rates.daily_cap IS 'Maximum charge per day regardless of duration';

-- Grant permissions (adjust as needed for your environment)
-- GRANT SELECT, INSERT, UPDATE, DELETE ON ALL TABLES IN SCHEMA public TO crudpark_app;
-- GRANT USAGE, SELECT ON ALL SEQUENCES IN SCHEMA public TO crudpark_app;

-- Display summary
SELECT 'Database schema created successfully!' as status;
SELECT 'Total operators: ' || COUNT(*) as info FROM operators;
SELECT 'Total memberships: ' || COUNT(*) as info FROM memberships;
SELECT 'Total rates: ' || COUNT(*) as info FROM rates;