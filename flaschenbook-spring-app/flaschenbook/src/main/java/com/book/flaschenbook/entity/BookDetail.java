package com.book.flaschenbook.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table
public class BookDetail {

    @EmbeddedId
    private BookDetailId id;

    @Column(name = "SALE_URL", length = 2000)
    private String saleUrl;

    @Column(name = "SALE_PRICE")
    private Integer salePrice;

    @Column(name = "SALE_STATUS", length = 10)
    private String saleStatus;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(length = 100)
    private String ranking;
}
