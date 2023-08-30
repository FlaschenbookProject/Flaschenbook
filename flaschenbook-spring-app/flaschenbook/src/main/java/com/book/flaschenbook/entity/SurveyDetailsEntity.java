package com.book.flaschenbook.entity;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "SurveyDetails")
public class SurveyDetailsEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int surveyDetailId;
    @ManyToOne
    @JoinColumn(name = "surveyId", referencedColumnName = "surveyId")
    private SurveysEntity survey;
    private String contentType;
    private String contentId;
}