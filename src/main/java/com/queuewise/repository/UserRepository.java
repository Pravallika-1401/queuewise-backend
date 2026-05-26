package com.queuewise.repository;

import com.queuewise.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    // Email tho user find cheyyi — login ki use avutundi
    Optional<User> findByEmail(String email);

    // Email already exists check cheyyi — register ki use avutundi
    boolean existsByEmail(String email);
}
