ALTER TABLE minio_files
    ADD COLUMN topic_id BIGINT;

ALTER TABLE minio_files
    ADD CONSTRAINT fk_topic_id
        FOREIGN KEY (topic_id) REFERENCES topics(id)
            ON DELETE CASCADE;
