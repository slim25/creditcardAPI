package com.interview.task.creditcardAPI.repository.jpa;

import com.interview.task.creditcardAPI.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
}
