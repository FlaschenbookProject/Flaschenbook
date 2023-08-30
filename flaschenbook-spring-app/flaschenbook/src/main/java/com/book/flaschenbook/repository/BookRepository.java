package com.book.flaschenbook.repository;

import com.book.flaschenbook.dto.BookInfoDTO;
import com.book.flaschenbook.entity.BookInfoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface BookRepository extends JpaRepository<BookInfoEntity, String> {
    @Query(value = """
    WITH book AS (
        SELECT a.*
             , (SELECT concat(depth2, ' > ', categoryName)
                  FROM BookCategory
                 WHERE a.categoryId = categoryId) categoryName
             , b.webCode
             , b.saleUrl
             , b.salePrice
             , b.saleStatus
             , b.description
             , b.ranking
          FROM BookInfo a
          JOIN BookDetail b
            ON a.isbn = b.isbn
         WHERE DATE_FORMAT(a.pubDate, '%Y%m') = date_format(now(), '%Y%m')
           AND a.categoryId IN (SELECT code
                                  FROM CodeDetail
                                 WHERE commonCode = 1)
        ORDER BY a.pubDate DESC
    )
    SELECT isbn, title, categoryName, webCode, saleUrl, salePrice, saleStatus, description, ranking
    FROM book
    LIMIT 30
    """, nativeQuery = true)
    List<BookInfoDTO> findNewReleases();
}