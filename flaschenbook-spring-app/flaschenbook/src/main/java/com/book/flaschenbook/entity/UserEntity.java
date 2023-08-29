package com.book.flaschenbook.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.sql.Timestamp;

@Data
@Entity
@Table(name = "Users")
public class UserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int userId;
    private String username;
    @Column(unique = true)
    private String email;
    private String passwordHash;
    private String profileImageUrl;
    private String sex;
    private Timestamp createdAt;
    private Timestamp lastLogin;
    private String googleAuthToken;
}