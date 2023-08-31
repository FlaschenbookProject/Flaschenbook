package com.book.flaschenbook.repository;

import com.book.flaschenbook.entity.BookContentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BookContentRepository extends JpaRepository<BookContentEntity, Integer> {
}
