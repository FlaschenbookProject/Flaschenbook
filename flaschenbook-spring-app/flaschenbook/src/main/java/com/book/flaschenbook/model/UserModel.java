package com.book.flaschenbook.model;

import lombok.Data;
import java.sql.Timestamp;

@Data
public class UserModel {

    private int userId;
    private String password;
    private String username;
    private String email;
    private String profileImageUrl;
    private String sex;
    private Timestamp createdAt;
    private Timestamp lastLogin;

}