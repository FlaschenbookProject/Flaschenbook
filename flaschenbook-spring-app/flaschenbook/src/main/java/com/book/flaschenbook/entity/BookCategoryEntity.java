package com.book.flaschenbook.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Column;
import jakarta.persistence.Table;
import java.io.Serializable;

@Entity
@Table(name = "BookCategory")
public class BookCategoryEntity implements Serializable {

    @Id
    private Integer categoryId;

    @Column(length = 50)
    private String categoryName;

    @Column (length = 50)
    private String depth1;

    @Column(length = 50)
    private String depth2;

    @Column(length = 50)
    private String depth3;

    @Column(length = 50)
    private String depth4;

    @Column(length = 50)
    private String depth5;
}
