package com.multigenesystask.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserResponse {
    private String message;
    private String email;
    private String firstName;
    private String lastName;
    private String role;
}