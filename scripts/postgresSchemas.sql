CREATE TABLE IF NOT EXISTS accounts
(
    id SERIAL PRIMARY KEY,
    given_name TEXT NOT NULL,
    family_name TEXT NOT NULL,
    email_address TEXT UNIQUE,
    phone_number TEXT UNIQUE,
    reward_points INTEGER CHECK(reward_points >= 0),
    balance INTEGER CHECK(balance >= 0)
);

CREATE TABLE IF NOT EXISTS passwords
(
    id SERIAL PRIMARY KEY REFERENCES accounts,
    password TEXT NOT NULL
);

CREATE TABLE IF NOT EXISTS google_accounts
(
    id SERIAL PRIMARY KEY REFERENCES accounts,
    google_id TEXT UNIQUE
);

CREATE TABLE IF NOT EXISTS third_party_accounts
(
    id TEXT PRIMARY KEY,
    secret TEXT NOT NULL,
    creation_time TIMESTAMP
);

CREATE TABLE IF NOT EXISTS authorization_codes
(
    code TEXT PRIMARY KEY,
    account_id INT REFERENCES accounts,
    third_party_account_id TEXT REFERENCES third_party_accounts,
    valid BOOLEAN
);

CREATE TABLE IF NOT EXISTS refresh_tokens
(
    token TEXT PRIMARY KEY,
    account_id INT REFERENCES accounts,
    third_party_account_id TEXT REFERENCES third_party_accounts,
    valid BOOLEAN
);
