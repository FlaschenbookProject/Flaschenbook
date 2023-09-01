package com.book.flaschenbook.repository;

import com.book.flaschenbook.entity.SurveyContentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SurveyContentRepository extends JpaRepository<SurveyContentEntity, Long> {
    List<SurveyContentEntity> findByType(String type);

}
