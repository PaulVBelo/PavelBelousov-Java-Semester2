ALTER TABLE books
ADD rating INTEGER DEFAULT 0
CONSTRAINT rating_bounds_ck
CHECK (rating BETWEEN 0 AND 10);