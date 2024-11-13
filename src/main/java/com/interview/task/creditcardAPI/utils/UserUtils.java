package com.interview.task.creditcardAPI.utils;

import com.interview.task.creditcardAPI.model.User;
import com.interview.task.creditcardAPI.repository.jpa.UserRepository;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

@Component
public class UserUtils {

    private final UserRepository userRepository;

    @Autowired
    public UserUtils(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public String getCurrentUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            throw new UsernameNotFoundException("User not authenticated");
        }

        return authentication.getName();
    }

    public Long getCurrentUserId() {
        String username = getCurrentUsername();

        return getCurrentUserId(username);
    }

    public Long getCurrentUserId(String username) {
        if (StringUtils.isNotBlank(username)) {
            return userRepository.findByUsername(username)
                    .map(User::getId)
                    .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        }
        throw new UsernameNotFoundException("username is empty");
    }
}
