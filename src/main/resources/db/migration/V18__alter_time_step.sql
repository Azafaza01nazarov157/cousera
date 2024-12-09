ALTER TABLE courses
ALTER COLUMN create_at TYPE TIMESTAMP
USING create_at::timestamp;

ALTER TABLE courses
ALTER COLUMN update_at TYPE TIMESTAMP
USING update_at::timestamp;
