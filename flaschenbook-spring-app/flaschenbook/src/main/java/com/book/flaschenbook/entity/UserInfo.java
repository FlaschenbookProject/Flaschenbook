package com.book.flaschenbook.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "UserInfo")
public class UserInfo {

    @Id
    @Column(name = "EMAIL")
    private String email;

    @Column(name = "USER_ID", unique = true)
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer userId;

    @Column(name = "SEX")
    private String sex;

    @Column(name = "BIRTHDATE")
    private LocalDateTime birthdate;
}
