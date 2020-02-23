CREATE TABLE accounts (
  account_id    BIGINT      PRIMARY KEY,
  amount        DECIMAL     NOT NULL CHECK (amount >= 0.0)
);