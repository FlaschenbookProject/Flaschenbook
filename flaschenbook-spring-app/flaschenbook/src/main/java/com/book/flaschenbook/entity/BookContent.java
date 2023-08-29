package com.book.flaschenbook.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Data
@Table(name = "BookContent")
public class BookContent {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "CONTENT_ID")
    private Integer contentId;

    @Column(name = "ISBN")
    private String isbn;

    @Column(name = "CONTENT")
    private String content;
}
