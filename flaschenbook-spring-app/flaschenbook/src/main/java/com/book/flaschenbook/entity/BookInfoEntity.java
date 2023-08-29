package com.book.flaschenbook.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Id;
import jakarta.persistence.Column;
import jakarta.persistence.Table;
import jakarta.persistence.CascadeType;
import jakarta.persistence.FetchType;
import java.util.List;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import lombok.Data;

@Entity
@Data
@Table(name = "BookInfo")
public class BookInfoEntity implements Serializable {

    @Id
    @Column(length = 13)
    private String isbn;

    @OneToMany(mappedBy = "bookInfo", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<BookDetailEntity> bookDetails;


    @Column(length = 700)
    private String title;
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