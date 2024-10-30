package org.example.cursera.domain.repository;

import org.example.cursera.domain.entity.MinioFile;
import org.example.cursera.domain.entity.Topic;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MinioFileRepository extends JpaRepository<MinioFile, Integer> {
    List<MinioFile> findByTopic(Topic topic);
}