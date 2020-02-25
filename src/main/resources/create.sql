CREATE TABLE accounts (
  account_id    BIGINT      PRIMARY KEY,
  amount        DECIMAL     NOT NULL CHECK (amount >= 0.0)
);

CREATE TABLE transfers (
  transaction_id    VARCHAR(255)    PRIMARY KEY,
  account_id        BIGINT          NOT NULL,
  receiver_id       BIGINT          NOT NULL,
  amount            DECIMAL(20,2)   NOT NULL CHECK (amount >= 0.0),
  done_at           TIMESTAMP       NOT NULL,

  FOREIGN KEY (account_id) REFERENCES accounts (account_id),
  FOREIGN KEY (receiver_id) REFERENCES accounts (account_id)
);