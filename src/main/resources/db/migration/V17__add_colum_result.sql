-- Добавляем новый столбец "analysis_id" в таблицу "test_results"
ALTER TABLE test_results
    ADD COLUMN analysis_id BIGINT;

-- Создаем внешний ключ, который будет ссылаться на таблицу "analysis"
ALTER TABLE test_results
    ADD CONSTRAINT fk_analysis_id
        FOREIGN KEY (analysis_id) REFERENCES analysis(id)
            ON DELETE SET NULL;  -- или "ON DELETE CASCADE", если хотите, чтобы при удалении записи в таблице "analysis" связанные записи в "test_results" удалялись.
