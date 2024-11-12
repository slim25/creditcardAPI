package com.interview.task.creditcardAPI.service.impl;

import com.interview.task.creditcardAPI.model.UserActivityLog;
import com.interview.task.creditcardAPI.repository.mongo.UserActivityLogRepository;
import com.interview.task.creditcardAPI.service.UserActivityLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class UserActivityLogServiceImpl implements UserActivityLogService {

    @Autowired
    private UserActivityLogRepository activityLogRepository;

    @Override
    public void logActivity(Long userId, String activityType, String details) {
        UserActivityLog log = new UserActivityLog();
        log.setUserId(userId);
        log.setActivityType(activityType);
        log.setTimestamp(LocalDateTime.now());
        log.setDetails(details);

        activityLogRepository.save(log);
    }
}
