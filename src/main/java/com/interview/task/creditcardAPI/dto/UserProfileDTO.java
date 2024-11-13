package com.interview.task.creditcardAPI.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class UserProfileDTO  implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;
    private String name;
    private String email;
    private String phone;
    private Long userId;
}
