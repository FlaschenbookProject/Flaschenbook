package com.book.flaschenbook.repository;

import com.book.flaschenbook.entity.SurveyDetailsEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SurveyDetailsRepository extends JpaRepository<SurveyDetailsEntity, Integer> {
}
