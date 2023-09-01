package com.book.flaschenbook.repository;

import com.book.flaschenbook.entity.SurveyDetailsEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SurveyDetailsRepository extends JpaRepository<SurveyDetailsEntity, Integer> {
    List<SurveyDetailsEntity> findBySurveyId(int surveyId);
}
