package com.book.flaschenbook.repository;

import com.book.flaschenbook.entity.SurveysEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SurveysRepository extends JpaRepository<SurveysEntity, Integer> {
    List<SurveysEntity> findByUserId(Integer userId);
}


