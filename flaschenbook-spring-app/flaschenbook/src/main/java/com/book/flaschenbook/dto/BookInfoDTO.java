package com.book.flaschenbook.dto;

import jakarta.persistence.Column;
import lombok.Data;
import java.math.BigDecimal;
import java.util.Date;

@Data
public class BookInfoDTO {
    private String isbn;
    private String title;
    private Integer categoryId;
    private String author;
    private String translator;
    private String publisher;
    private Date pubDate;
    private BigDecimal price;
    private Integer pageCnt;
    private String imageUrl;
    private String categoryName;
    private String webCode;
    private Integer saleUrl;
    private Integer salePrice;
    private String saleStatus;
    private Integer description;
    private String ranking;
}
