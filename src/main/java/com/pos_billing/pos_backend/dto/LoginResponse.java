package com.pos_billing.pos_backend.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class LoginResponse {
    private String message;
    private boolean success;
    private String role;   // Added role field for ui friendly
}
