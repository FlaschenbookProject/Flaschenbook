package com.book.flaschenbook.repository;

import com.book.flaschenbook.entity.BookCategoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BookCategoryRepository extends JpaRepository<BookCategoryEntity, Integer> {

    Optional<BookCategoryEntity> findByCategoryId(Integer categoryId);
}
