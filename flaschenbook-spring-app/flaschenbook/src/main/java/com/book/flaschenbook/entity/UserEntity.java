package com.book.flaschenbook.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.Date;

@Data
@Entity
@Table(name = "Users")
public class UserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int userId;
    private String username;
    @Column(unique = true, length = 254)
    private String email;
    @Column(length = 512)
    private String passwordHash;
    @Column(length = 2048)
    private String profileImageUrl;
    private String gender;
    private Date birthdate;
    private LocalDateTime createdAt;
    private LocalDateTime lastLogin;
    @Column(length = 2048)
    private String googleAuthToken;

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
    }
}

