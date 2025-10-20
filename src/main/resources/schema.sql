-- schema.sql - Place in src/main/resources/

-- Accounts table (Write Model)
CREATE TABLE IF NOT EXISTS accounts (
    account_id VARCHAR(255) PRIMARY KEY,
    account_holder VARCHAR(255) NOT NULL,
    balance DECIMAL(19, 2) NOT NULL,
    status VARCHAR(50) NOT NULL,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL
);

CREATE INDEX idx_accounts_status ON accounts(status);
CREATE INDEX idx_accounts_created_at ON accounts(created_at);

-- Event Store
CREATE TABLE IF NOT EXISTS event_store (
    id SERIAL PRIMARY KEY,
    account_id VARCHAR(255) NOT NULL,
    event_type VARCHAR(100) NOT NULL,
    event_data TEXT NOT NULL,
    occurred_at TIMESTAMP NOT NULL,
    stored_at TIMESTAMP NOT NULL
);

CREATE INDEX idx_event_store_account_id ON event_store(account_id);
CREATE INDEX idx_event_store_occurred_at ON event_store(occurred_at);
CREATE INDEX idx_event_store_event_type ON event_store(event_type);
