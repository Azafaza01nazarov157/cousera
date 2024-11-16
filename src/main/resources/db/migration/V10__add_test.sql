ALTER TABLE test_results
    ADD CONSTRAINT fk_test_results_test
        FOREIGN KEY (test_id) REFERENCES tests(id) ON DELETE CASCADE;