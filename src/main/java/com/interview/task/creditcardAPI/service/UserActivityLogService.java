package com.interview.task.creditcardAPI.service;

public interface UserActivityLogService {
    void logActivity(Long userId, String activityType, String details);
}
