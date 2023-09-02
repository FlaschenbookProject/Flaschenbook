package com.book.flaschenbook.entity;

import jakarta.persistence.*;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import lombok.Data;

@Entity
@Data
@Table(name = "BookReview")
public class BookReviewEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer reviewId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "isbn", insertable = false, updatable = false)
    private BookInfoEntity bookInfo;

    @Column(length = 700)
    private String isbn;

    @Column(length = 2)
    private String webCode;

    private Date wrtDate;

    @Column(columnDefinition = "LONGTEXT")
    private String content;

    @Column(precision = 3, scale = 1)
    private BigDecimal rating;
}