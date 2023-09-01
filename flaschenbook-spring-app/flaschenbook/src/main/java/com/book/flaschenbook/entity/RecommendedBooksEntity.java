package com.book.flaschenbook.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.sql.Date;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "RecommendedBooks")
public class RecommendedBooksEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private Integer userId;
    private String isbn;

    @Column(columnDefinition = "DATE")
    private Date recommendDate;

    @Column(columnDefinition = "BOOLEAN DEFAULT FALSE")
    private Boolean isRead;

    @Column(columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime eventTime;

    @PrePersist
    public void prePersist() {
        if (recommendDate == null) {
            recommendDate = Date.valueOf(LocalDate.now());
        }
        if (eventTime == null) {
            eventTime = LocalDateTime.now();
        }
        if (isRead == null) {
            isRead = false;
        }
    }
}