package com.interview.task.creditcardAPI.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@Configuration
@EnableMongoRepositories(basePackages = "com.interview.task.creditcardAPI.repository.mongo")
public class MongoConfig {

}
