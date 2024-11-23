package org.example.cursera.domain.repository;

import org.example.cursera.domain.entity.MinioFile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MinioFileRepository extends JpaRepository<MinioFile, Integer> {
}