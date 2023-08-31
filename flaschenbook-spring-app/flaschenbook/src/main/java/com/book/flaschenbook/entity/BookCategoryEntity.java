package com.book.flaschenbook.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
@Entity
@Table(name = "BookCategory")
public class BookCategoryEntity implements Serializable {

    @Id
    private Integer categoryId;

    @OneToMany(mappedBy = "category", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<BookInfoEntity> books;

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
