package com.book.flaschenbook.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.Formula;
import lombok.Data;

@Entity
@Data
@Table(name = "BookDetail")
public class BookDetailEntity {

    @EmbeddedId
    private BookDetailIdEntity id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("isbn")
    @JoinColumn(name = "isbn")
    private BookInfoEntity bookInfo;

    private String saleUrl;

    private Integer salePrice;

    private String saleStatus;

    @Column(columnDefinition = "TEXT")
    private String description;

    private String ranking;
}
