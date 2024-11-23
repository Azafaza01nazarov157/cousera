ALTER TABLE users
    ADD COLUMN photo_id INT;

ALTER TABLE users
    ADD CONSTRAINT fk_users_photo
        FOREIGN KEY (photo_id)
            REFERENCES minio_files (id)
            ON DELETE SET NULL;
