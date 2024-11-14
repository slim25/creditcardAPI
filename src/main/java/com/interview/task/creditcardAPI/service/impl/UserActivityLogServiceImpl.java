package com.interview.task.creditcardAPI.service.impl;

import com.interview.task.creditcardAPI.model.UserActivityLog;
import com.interview.task.creditcardAPI.repository.mongo.UserActivityLogRepository;
import com.interview.task.creditcardAPI.service.UserActivityLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class UserActivityLogServiceImpl implements UserActivityLogService {

    private final UserActivityLogRepository activityLogRepository;
    @Autowired
    public UserActivityLogServiceImpl(UserActivityLogRepository activityLogRepository) {
        this.activityLogRepository = activityLogRepository;
    }

    @Override
    public void logActivity(Long userId, String activityType, String details) {
        UserActivityLog log = new UserActivityLog();
        log.setUserId(userId);
        log.setActivityType(activityType);
        log.setTimestamp(LocalDateTime.now());
        log.setDetails(details);

        activityLogRepository.save(log);
    }
    @Cacheable(value = "activityLogs", key = "#userId")
    @Override
    public List<UserActivityLog> getRecentActivitiesByUser(Long userId, int limit) {
        return activityLogRepository.findByUserId(userId, PageRequest.of(0, limit, Sort.by(Sort.Direction.DESC, "timestamp")));
    }
}
