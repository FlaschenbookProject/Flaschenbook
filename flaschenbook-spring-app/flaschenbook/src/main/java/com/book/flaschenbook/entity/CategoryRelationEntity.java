package com.book.flaschenbook.entity;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "CategoryRelation")
public class CategoryRelationEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int relationId;
    private int oldCategoryId;
    private int newCategoryId;
}
