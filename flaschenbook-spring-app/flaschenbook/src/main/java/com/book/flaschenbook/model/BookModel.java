package com.book.flaschenbook.model;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class BookModel {
    private String isbn;
    private String title;
    private Integer categoryId;
    private String categoryName;
    private String author;
    private String translator;
    private String publisher;
    private Date pubDate;
    private BigDecimal price;
    private Integer pageCnt;
    private String imageUrl;
    private String ranking;

    private String description;
    private String saleUrl;
    private Integer salePrice;
    private String saleStatus;
}
