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
public class BookContentEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer contentId;

    
    private String isbn;
    private String content;
}
