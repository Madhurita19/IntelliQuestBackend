package com.intelliquest.repository;

import com.intelliquest.model.User; 
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    long countByRole(User.UserRole role);
    boolean existsByEmail(String email);
    Optional<User> findById(Long id);
    void deleteById(Long id);
    long countByEnabledTrue();

    
}
