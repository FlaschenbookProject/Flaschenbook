package com.book.flaschenbook.entity;

import jakarta.persistence.Entity;
import org.hibernate.annotations.Formula;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Data
@Table(name = "BookDetail")
public class BookDetailEntity {

    @EmbeddedId
    private BookDetailIdEntity id;

    @Formula("(CASE WHEN webCode = 'AL' THEN aladin_saleUrl WHEN webCode = 'NA' THEN naver_saleUrl WHEN webCode = 'KA' THEN kakao_saleUrl ELSE NULL END)")
    private String saleUrl;

    @Formula("(CASE WHEN webCode = 'AL' THEN aladin_salePrice WHEN webCode = 'NA' THEN naver_salePrice WHEN webCode = 'KA' THEN kakao_salePrice ELSE NULL END)")
    private Integer salePrice;

    @Formula("(CASE WHEN webCode = 'AL' THEN aladin_saleStatus WHEN webCode = 'NA' THEN naver_saleStatus WHEN webCode = 'KA' THEN kakao_saleStatus ELSE NULL END)")
    private String saleStatus;

    @Formula("(CASE WHEN webCode = 'AL' THEN aladin_description WHEN webCode = 'NA' THEN naver_description WHEN webCode = 'KA' THEN kakao_description ELSE NULL END)")
    private String description;

    private String ranking;
}
