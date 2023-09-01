package com.book.flaschenbook.repository;

import com.book.flaschenbook.entity.CodeDataEntity;
import com.book.flaschenbook.entity.CodeDetailEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CodeDetailRepository extends JpaRepository<CodeDetailEntity, Integer> {

    List<CodeDetailEntity> findByCommonCode(int i);
}

