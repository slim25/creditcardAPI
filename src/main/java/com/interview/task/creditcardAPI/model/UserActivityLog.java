package com.interview.task.creditcardAPI.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Document(collection = "user_activity_logs")
public class UserActivityLog {

    @Id
    private String id;

    private Long userId;
    private String activityType;
    private LocalDateTime timestamp;
    private String details;
}