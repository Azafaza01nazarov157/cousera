ALTER TABLE test_results
    ADD COLUMN lesson_id BIGINT;

ALTER TABLE test_results
    ADD CONSTRAINT fk_lesson_id
        FOREIGN KEY (lesson_id)
            REFERENCES lessons (id)
            ON DELETE SET NULL;
