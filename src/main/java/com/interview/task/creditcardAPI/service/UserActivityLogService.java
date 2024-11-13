package com.interview.task.creditcardAPI.service;

import com.interview.task.creditcardAPI.model.UserActivityLog;

import java.util.List;

public interface UserActivityLogService {
    void logActivity(Long userId, String activityType, String details);
    List<UserActivityLog> getRecentActivitiesByUser(Long userId, int limit);
}
