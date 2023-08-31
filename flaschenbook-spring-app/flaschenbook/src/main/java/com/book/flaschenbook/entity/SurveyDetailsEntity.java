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
    private int surveyId;
    private String contentType;
    private String contentId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "contentId",referencedColumnName = "isbn", insertable = false, updatable = false)
    private BookInfoEntity book;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "contentId",referencedColumnName = "contentId", insertable = false, updatable = false)
    private BookContentEntity bookContent;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "surveyId", referencedColumnName = "surveyId", insertable = false, updatable = false)
    private SurveysEntity survey;
}