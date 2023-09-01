package com.book.flaschenbook.repository;

import com.book.flaschenbook.entity.SurveyContentEntity;
import com.book.flaschenbook.entity.SurveyContentId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SurveyContentRepository extends JpaRepository<SurveyContentEntity, SurveyContentId> {
    List<SurveyContentEntity> findByType(String type);

}
