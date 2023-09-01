package com.book.flaschenbook.entity;

import jakarta.persistence.*;
import lombok.Data;
@Data
@Entity
@IdClass(SurveyContentId.class)
@Table(name = "SurveyContent")
public class SurveyContentEntity {
    @Id
    private String id;

    @Id
    private String type;

    private String content;
}

