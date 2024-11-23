ALTER TABLE lessons
    ADD COLUMN file_id INT;

ALTER TABLE lessons
    ADD CONSTRAINT fk_lessons_file
        FOREIGN KEY (file_id)
            REFERENCES minio_files (id)
            ON DELETE SET NULL;


ALTER TABLE courses
    ADD COLUMN image_id INT;

ALTER TABLE courses
    ADD CONSTRAINT fk_courses_image
        FOREIGN KEY (image_id)
            REFERENCES minio_files (id)
            ON DELETE SET NULL;
