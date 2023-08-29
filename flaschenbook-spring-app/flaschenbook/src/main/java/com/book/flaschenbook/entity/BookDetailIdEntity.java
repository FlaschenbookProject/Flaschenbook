package com.book.flaschenbook.entity;

import jakarta.persistence.Embeddable;
import java.io.Serializable;

@Embeddable
public class BookDetailIdEntity implements Serializable {

    private String isbn;
    private String webCode;

}