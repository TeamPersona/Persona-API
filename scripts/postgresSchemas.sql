CREATE TABLE accounts
(
    id SERIAL PRIMARY KEY,
    given_name TEXT NOT NULL,
    family_name TEXT NOT NULL,
    email_address TEXT UNIQUE,
    phone_number TEXT UNIQUE
);

CREATE TABLE passwords
(
    id SERIAL PRIMARY KEY REFERENCES accounts,
    password TEXT NOT NULL
);

CREATE TABLE google_accounts
(
    id SERIAL PRIMARY KEY REFERENCES accounts,
    google_id TEXT UNIQUE
);

CREATE TABLE third_party_accounts
(
    id TEXT PRIMARY KEY,
    creation_time TIMESTAMP
);

CREATE TABLE refresh_tokens
(
    token TEXT PRIMARY KEY,
    account_id INT REFERENCES accounts,
    third_party_account_id TEXT REFERENCES third_party_accounts,
    valid BOOLEAN
);
