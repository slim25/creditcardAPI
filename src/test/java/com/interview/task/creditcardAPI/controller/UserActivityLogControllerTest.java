package com.interview.task.creditcardAPI.controller;

import com.interview.task.creditcardAPI.model.UserActivityLog;
import com.interview.task.creditcardAPI.service.UserActivityLogService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

public class UserActivityLogControllerTest {

    @InjectMocks
    private UserActivityLogController userActivityLogController;

    @Mock
    private UserActivityLogService userActivityLogService;

    private UserActivityLog activityLog;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        activityLog = new UserActivityLog();
        activityLog.setUserId(1L);
        activityLog.setActivityType("LOGIN");
        activityLog.setTimestamp(LocalDateTime.now());
        activityLog.setDetails("User logged in");
    }

    @Test
    public void getUserActivityLogs_Success() {
        Long userId = 1L;
        int limit = 5;
        when(userActivityLogService.getRecentActivitiesByUser(userId, limit)).thenReturn(List.of(activityLog));

        ResponseEntity<List<UserActivityLog>> response = userActivityLogController.getUserActivityLogs(userId, limit);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
        assertEquals(activityLog, response.getBody().get(0));
    }

    @Test
    public void getUserActivityLogs_EmptyResult() {
        Long userId = 1L;
        int limit = 5;
        when(userActivityLogService.getRecentActivitiesByUser(userId, limit)).thenReturn(List.of());

        ResponseEntity<List<UserActivityLog>> response = userActivityLogController.getUserActivityLogs(userId, limit);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().isEmpty());
    }

    @Test
    public void getUserActivityLogs_UserNotFound() {
        Long nonExistentUserId = 999L;
        int limit = 5;
        when(userActivityLogService.getRecentActivitiesByUser(nonExistentUserId, limit)).thenThrow(new NoSuchElementException("User not found"));

        assertThrows(NoSuchElementException.class, () -> {
            userActivityLogController.getUserActivityLogs(nonExistentUserId, limit);
        });
    }
}
