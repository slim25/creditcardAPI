package com.interview.task.creditcardAPI.dto;

import lombok.Data;

@Data
public class UserProfileDTO {
    private Long id;
    private String name;
    private String email;
    private String phone;
}
