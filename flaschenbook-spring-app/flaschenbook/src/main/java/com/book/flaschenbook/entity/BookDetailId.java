package com.book.flaschenbook.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Data;

import java.io.Serializable;

@Embeddable
@Data
public class BookDetailId implements Serializable {

    private String isbn;
    @Column(name = "WEB_CODE")
    private String webCode;
}
