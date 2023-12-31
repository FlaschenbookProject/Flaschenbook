package com.book.flaschenbook.repository;

import com.book.flaschenbook.entity.CodeDataEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CodeDataRepository extends JpaRepository<CodeDataEntity, Integer> {

}

