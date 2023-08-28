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
@Table(name = "BookReview")
public class BookReview {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "REVIEW_ID")
    private Integer reviewId;

    @Column(name = "ISBN")
    private String isbn;

    @Column(name = "WEB_CODE")
    private String webCode;

    @Column(name = "WRT_DATE")
    private LocalDateTime wrtDate;

    @Column(name = "CONTENT", columnDefinition="LONGTEXT")
    private String content;

    @Column(name = "RATING")
    private Double rating;
}
