package com.interview.task.creditcardAPI.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EnableJpaRepositories(basePackages = "com.interview.task.creditcardAPI.repository.jpa")
public class JpaConfig {

}
