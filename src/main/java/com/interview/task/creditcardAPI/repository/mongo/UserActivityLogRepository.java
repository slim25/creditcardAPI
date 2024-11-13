package com.interview.task.creditcardAPI.repository.mongo;

import com.interview.task.creditcardAPI.model.UserActivityLog;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface UserActivityLogRepository extends MongoRepository<UserActivityLog, String> {
    List<UserActivityLog> findByUserId(Long userId, Pageable pageable);
}
