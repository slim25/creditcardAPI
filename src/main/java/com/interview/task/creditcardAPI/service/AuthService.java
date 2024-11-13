package com.interview.task.creditcardAPI.service;

import com.interview.task.creditcardAPI.dto.LoginRequest;
import com.interview.task.creditcardAPI.model.User;

import java.util.Map;

public interface AuthService {
    void registerUser(User user);

    Map<String, String> getTokens(LoginRequest loginRequest);

    Map<String, String> validateRefreshToken(String refreshToken);

    void logout(String refreshToken);
}
