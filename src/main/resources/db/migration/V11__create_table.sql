
CREATE TABLE completed_lessons (
                                   lesson_id BIGINT NOT NULL,
                                   user_id BIGINT NOT NULL,
                                   PRIMARY KEY (lesson_id, user_id),
                                   CONSTRAINT fk_completed_lessons_lesson FOREIGN KEY (lesson_id)
                                       REFERENCES lessons (id) ON DELETE CASCADE,
                                   CONSTRAINT fk_completed_lessons_user FOREIGN KEY (user_id)
                                       REFERENCES users (id) ON DELETE CASCADE
);
