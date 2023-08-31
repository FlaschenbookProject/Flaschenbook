package com.book.flaschenbook.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "SurveySummary")
public class SurveySummaryEntity {

    @Id
    private int surveyId;

    private int userId;

    @Column(length = 13)
    private String contentId;

    @Column(columnDefinition = "MEDIUMTEXT")
    private String content;

    @Column(length = 1, nullable = false)
    private String type;

}