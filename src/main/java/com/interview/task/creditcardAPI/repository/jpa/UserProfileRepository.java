package com.interview.task.creditcardAPI.repository.jpa;

import com.interview.task.creditcardAPI.model.UserProfile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserProfileRepository extends JpaRepository<UserProfile, Long> {
    Optional<UserProfile> findByUserId(Long userId);

}
