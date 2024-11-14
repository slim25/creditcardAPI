package com.interview.task.creditcardAPI.service.impl;

import com.interview.task.creditcardAPI.model.UserActivityLog;
import com.interview.task.creditcardAPI.repository.mongo.UserActivityLogRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
@EnableCaching
class UserActivityLogServiceImplTest {

    @InjectMocks
    private UserActivityLogServiceImpl userActivityLogService;

    @Mock
    private UserActivityLogRepository activityLogRepository;

    private CacheManager cacheManager;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        cacheManager = new ConcurrentMapCacheManager("activityLogs");
        userActivityLogService = new UserActivityLogServiceImpl(activityLogRepository);
    }

    @Test
    void logActivity_ShouldSaveActivityLogWithCorrectDetails() {
        Long userId = 1L;
        String activityType = "LOGIN";
        String details = "User logged in";

        userActivityLogService.logActivity(userId, activityType, details);

        verify(activityLogRepository, times(1)).save(argThat(log ->
                log.getUserId().equals(userId) &&
                        log.getActivityType().equals(activityType) &&
                        log.getDetails().equals(details) &&
                        log.getTimestamp() != null));
    }

    @Test
    void getRecentActivitiesByUser_ShouldReturnEmptyListWhenNoLogsFound() {
        Long userId = 1L;
        int limit = 5;

        when(activityLogRepository.findByUserId(eq(userId), any(PageRequest.class)))
                .thenReturn(Collections.emptyList());

        List<UserActivityLog> result = userActivityLogService.getRecentActivitiesByUser(userId, limit);

        assertTrue(result.isEmpty(), "Expected no activity logs");
        verify(activityLogRepository, times(1)).findByUserId(eq(userId), any(PageRequest.class));
    }

    @Test
    void getRecentActivitiesByUser_ShouldSortLogsByTimestampDesc() {
        Long userId = 1L;
        int limit = 5;

        UserActivityLog log1 = new UserActivityLog();
        log1.setUserId(userId);
        log1.setActivityType("LOGIN");
        log1.setTimestamp(LocalDateTime.now().minusDays(1));

        UserActivityLog log2 = new UserActivityLog();
        log2.setUserId(userId);
        log2.setActivityType("LOGOUT");
        log2.setTimestamp(LocalDateTime.now());

        when(activityLogRepository.findByUserId(eq(userId), eq(PageRequest.of(0, limit, Sort.by(Sort.Direction.DESC, "timestamp")))))
                .thenReturn(List.of(log2, log1));

        List<UserActivityLog> result = userActivityLogService.getRecentActivitiesByUser(userId, limit);

        assertEquals(2, result.size());
        assertEquals("LOGOUT", result.get(0).getActivityType());
        assertEquals("LOGIN", result.get(1).getActivityType());

        verify(activityLogRepository, times(1)).findByUserId(eq(userId), any(PageRequest.class));
    }
}
