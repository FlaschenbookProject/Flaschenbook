package com.book.flaschenbook.repository;

import com.book.flaschenbook.entity.NewBookCategoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NewBookCategoryRepository extends JpaRepository<NewBookCategoryEntity, Integer> {

}
