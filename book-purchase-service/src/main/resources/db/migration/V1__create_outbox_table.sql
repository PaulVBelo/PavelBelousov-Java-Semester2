CREATE TABLE outbox
(
    id BIGSERIAL PRIMARY KEY,
    data TEXT NOT NULL
);

CREATE TABLE money
(
    deposit_id BIGSERIAL PRIMARY KEY,
    amount  DECIMAL(13, 2) CHECK(amount >= 0)
);

CREATE TABLE history
(
    id                  BIGSERIAL          PRIMARY KEY,
    request_id          BIGINT             NOT NULL,
    book_id             BIGINT             NOT NULL,
    book_title          TEXT               NOT NULL,
    author_first_name   TEXT               NOT NULL,
    author_last_name    TEXT               NOT NULL,
    success             BOOLEAN            NOT NULL
);