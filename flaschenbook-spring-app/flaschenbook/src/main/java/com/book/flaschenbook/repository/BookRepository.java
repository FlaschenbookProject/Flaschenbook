package com.book.flaschenbook.repository;

import com.book.flaschenbook.entity.BookInfoEntity;
import com.book.flaschenbook.model.BookModel;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Date;
import java.util.List;

public interface BookRepository extends JpaRepository<BookInfoEntity, String> {
    List<BookInfoEntity> findTop20ByPubDateBetweenOrderByPubDateDesc(Date startDate, Date endDate);
}

