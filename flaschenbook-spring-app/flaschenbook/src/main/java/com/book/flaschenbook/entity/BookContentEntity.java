package com.book.flaschenbook.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "BookContent")
public class BookContentEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long contentId;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("isbn")
    @JoinColumn(name = "isbn")
    private BookInfoEntity bookInfo;

    private String isbn;
    private String content;
}
