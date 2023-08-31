package com.book.flaschenbook.repository;

import com.book.flaschenbook.entity.SurveySummaryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SurveySummaryRepository extends JpaRepository<SurveySummaryEntity, Integer> {
    List<SurveySummaryEntity> findByType(String type);
    List<SurveySummaryEntity> findByTypeAndUserId(String type, int userId);
}
