package com.book.flaschenbook.entity;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "SurveySummary")
@IdClass(SurveySummaryKey.class)
public class SurveySummaryEntity {

    @Id
    private int surveyId;

    @Id
    private int userId;

    @Id
    @Column(length = 13)
    private String contentId;

    @Id
    @Column(length = 1, nullable = false)
    private String type;

    @Column(columnDefinition = "MEDIUMTEXT")
    private String content;
}
