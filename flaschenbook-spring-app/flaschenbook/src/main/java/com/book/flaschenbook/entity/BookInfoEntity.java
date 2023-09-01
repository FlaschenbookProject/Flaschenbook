package com.book.flaschenbook.entity;

import jakarta.persistence.*;

import java.util.List;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import lombok.Data;
import lombok.ToString;

@Entity
@Data
@ToString(exclude = {"category", "bookDetails"})
@Table(name = "BookInfo")
public class BookInfoEntity implements Serializable {

    @Id
    @Column(length = 13)
    private String isbn;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "categoryId", insertable = false, updatable = false)
    private BookCategoryEntity category;

    @OneToMany(mappedBy = "bookInfo", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<BookDetailEntity> bookDetails;

    @OneToMany(mappedBy = "bookInfo", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<BookContentEntity> bookContents;

    @Column(length = 700)
    private String title;

    @Column(name = "categoryId", insertable = false, updatable = false)
    private Integer categoryId;

    @Column(length = 700)
    private String author;

    @Column(length = 500)
    private String translator;

    @Column(length = 100)
    private String publisher;
    private Date pubDate;
    private BigDecimal price;
    private Integer pageCnt;

    @Column(length = 1000)
    private String imageUrl;
}