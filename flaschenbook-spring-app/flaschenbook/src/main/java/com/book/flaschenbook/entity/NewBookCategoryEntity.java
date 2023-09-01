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
    private int categoryId;
    private String categoryName;
}
