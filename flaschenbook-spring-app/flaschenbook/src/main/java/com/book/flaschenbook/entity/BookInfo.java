package com.book.flaschenbook.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Id;

import lombok.Builder;
import lombok.Data;

@Entity
@Data
@Builder
@Table
public class BookInfo {
    @Id
    @Column(name = "ISBN", length = 13)
    private String isbn;

    @Column(name = "TITLE", length = 700)
    private String title;

    @Column(name = "CATEGORY_ID")
    private Integer categoryId;

    @Column(name = "AUTHOR", length = 700)
    private String author;

    @Column(name = "TRANSLATOR", length = 500)
    private String translator;

    @Column(name = "PUBLISHER", length = 100)
    private String publisher;

    @Column(name = "PUBDATE")
    private LocalDateTime pubDate;

    @Column(name = "PRICE")
    private BigDecimal price;

    @Column(name = "PAGE_CNT")
    private Integer pageCnt;

    @Column(name = "IMAGE_URL", length = 1000)
    private String imageUrl;

}
