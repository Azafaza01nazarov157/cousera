CREATE TABLE test_results
(
    id         BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    test_id    BIGINT,
    user_id    BIGINT,
    score      INTEGER                                 NOT NULL,
    percentage DOUBLE PRECISION                        NOT NULL,
    is_correct BOOLEAN,
    CONSTRAINT pk_test_results PRIMARY KEY (id)
);

ALTER TABLE test_results
    ADD CONSTRAINT FK_TEST_RESULTS_ON_TEST FOREIGN KEY (test_id) REFERENCES tests (id);

ALTER TABLE test_results
    ADD CONSTRAINT FK_TEST_RESULTS_ON_USER FOREIGN KEY (user_id) REFERENCES users (id);