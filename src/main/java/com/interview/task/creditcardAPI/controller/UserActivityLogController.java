package com.interview.task.creditcardAPI.controller;

import com.interview.task.creditcardAPI.model.UserActivityLog;
import com.interview.task.creditcardAPI.service.UserActivityLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/activity-logs")
public class UserActivityLogController {

    @Autowired
    private UserActivityLogService activityLogService;

    @GetMapping("/user/{userId}/{limit}")
    public ResponseEntity<List<UserActivityLog>> getUserActivityLogs(@PathVariable Long userId, @PathVariable Integer limit) {
        List<UserActivityLog> logs = activityLogService.getRecentActivitiesByUser(userId, limit);
        return ResponseEntity.ok(logs);
    }
}
