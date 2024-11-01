CREATE TABLE subscription_requests (
                                       id BIGSERIAL PRIMARY KEY,
                                       course_id BIGINT NOT NULL,
                                       user_id BIGINT NOT NULL,
                                       status VARCHAR(255),

                                       CONSTRAINT fk_course
                                           FOREIGN KEY (course_id)
                                               REFERENCES courses (id),

                                       CONSTRAINT fk_user
                                           FOREIGN KEY (user_id)
                                               REFERENCES users (id)
);

-- Add indexes for faster lookups if needed
CREATE INDEX idx_course_id ON subscription_requests (course_id);
CREATE INDEX idx_user_id ON subscription_requests (user_id);