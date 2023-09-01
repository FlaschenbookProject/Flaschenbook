package com.book.flaschenbook.repository;

import com.book.flaschenbook.entity.BookDetailEntity;
import com.book.flaschenbook.entity.BookDetailIdEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BookDetailRepository extends JpaRepository<BookDetailEntity, BookDetailIdEntity> {

}
