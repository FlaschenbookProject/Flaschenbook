package com.book.flaschenbook.repository;


import java.sql.Date;
import java.util.List;

import com.book.flaschenbook.entity.RecommendedBooksEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RecommendedBooksRepository extends JpaRepository<RecommendedBooksEntity, Integer> {
    Page<RecommendedBooksEntity> findByUserId(int userId, Pageable pageable);
    RecommendedBooksEntity findByUserIdAndRecommendDate(int userId, Date recommendDate);
}