package com.book.flaschenbook.model;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class UserModel {

    private int userId;
    private String username;
    private String email;
    private String password;
    private String profileImageUrl;
    private String gender;
    private LocalDateTime createdAt;
    private LocalDateTime lastLogin;
    private String sessionId;
}
