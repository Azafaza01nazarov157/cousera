ALTER TABLE minio_files
    ADD COLUMN IF NOT EXISTS topic_id BIGINT;

ALTER TABLE minio_files
    ALTER COLUMN topic_id SET NOT NULL;

ALTER TABLE minio_files
    ADD CONSTRAINT fk_minio_files_topic
        FOREIGN KEY (topic_id)
            REFERENCES topics (id)
            ON DELETE CASCADE;
