package com.queuewise.dto;

import lombok.Data;

// DTO = Data Transfer Object
// Entity direct ga expose cheyyadam dangerous — DTO use chesthamu

public class AuthDTOs {

    @Data
    public static class RegisterRequest {
        private String name;
        private String email;
        private String password;
        private String role; // "ADMIN" or "USER" — default: USER
    }

    @Data
    public static class LoginRequest {
        private String email;
        private String password;
    }

    @Data
    public static class AuthResponse {
        private String token;
        private String name;
        private String email;
        private String role;
        private Long userId;

        public AuthResponse(String token, String name, String email, String role, Long userId) {
            this.token = token;
            this.name = name;
            this.email = email;
            this.role = role;
            this.userId = userId;
        }
    }
}
