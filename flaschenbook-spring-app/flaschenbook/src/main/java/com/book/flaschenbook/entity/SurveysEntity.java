package com.book.flaschenbook.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "Surveys")
public class SurveysEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int surveyId;
    private int userId;
    private LocalDateTime eventTime;

    @PrePersist
    public void prePersist() {
        if (eventTime == null) {
            eventTime = LocalDateTime.now();
        }
    }

}
