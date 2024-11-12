package com.interview.task.creditcardAPI.repository.mongo;

import com.interview.task.creditcardAPI.model.UserActivityLog;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface UserActivityLogRepository extends MongoRepository<UserActivityLog, String> {

}
