package com.book.flaschenbook.repository;

import com.book.flaschenbook.entity.BookCategoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BookCategoryRepository extends JpaRepository<BookCategoryEntity, Integer> {

    Optional<BookCategoryEntity> findByCategoryId(Integer categoryId);
}
