CREATE TABLE authors
(
    id                  BIGSERIAL       PRIMARY KEY,
    first_name           TEXT                NOT NULL,
    last_name            TEXT                NOT NULL
);

CREATE TABLE books
(
   id               BIGSERIAL                                                             PRIMARY KEY,
   author_id        BIGINT          REFERENCES authors (id)  ON DELETE CASCADE               NOT NULL,
   title             TEXT                                                                    NOT NULL
);
CREATE TABLE tags
(
    id              BIGSERIAL       PRIMARY KEY,
    name            TEXT            NOT NULL UNIQUE
);
CREATE TABLE tag_book
(
    book_id         BIGINT          REFERENCES books (id)  ON DELETE CASCADE                  NOT NULL,
    tag_id          BIGINT          REFERENCES tags (id)   ON DELETE CASCADE                  NOT NULL
);
