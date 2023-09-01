package com.book.flaschenbook.repository;

import com.book.flaschenbook.entity.CategoryRelationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoryRelationRepository extends JpaRepository<CategoryRelationEntity, Integer> {
}
