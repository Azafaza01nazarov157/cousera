package org.example.cursera.domain.repository;

import org.example.cursera.domain.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
    
    @Query("SELECT u.id, u.email, u.password, u.active, u.checkOtp FROM User u WHERE u.email = :email")
    Optional<User> findByEmailWithoutPhoto(@Param("email") String email);

    Optional<User> findByEmail(String email);
}