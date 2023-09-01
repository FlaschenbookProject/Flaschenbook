package com.book.flaschenbook.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "NewBookCategory")
public class NewBookCategoryEntity {
    @Id
    private Long categoryId;
    private String categoryName;
}
