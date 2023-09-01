package com.book.flaschenbook.repository;

import com.book.flaschenbook.entity.BookInfoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookInfoRepository extends JpaRepository<BookInfoEntity, String> {
    BookInfoEntity findByIsbn(String isbn);

    @Query("SELECT b " +
            "FROM BookInfoEntity b " +
            "JOIN CategoryRelationEntity c ON b.categoryId = c.oldCategoryId " +
            "WHERE c.newCategoryId IN :newCategoryIds")
    List<BookInfoEntity> findByNewCategoryIds(@Param("newCategoryIds") List<Integer> newCategoryIds);
}
