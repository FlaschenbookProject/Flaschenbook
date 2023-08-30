package com.book.flaschenbook.entity;

import jakarta.persistence.Embeddable;
import lombok.Data;

import java.io.Serializable;

@Embeddable
@Data
public class BookDetailIdEntity implements Serializable {

    private String isbn;
    private String webCode;

}