package com.book.flaschenbook.repository;

import com.book.flaschenbook.entity.BookInfoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BookInfoRepository extends JpaRepository<BookInfoEntity, String> {
    BookInfoEntity findByIsbn(String contentId);
}
