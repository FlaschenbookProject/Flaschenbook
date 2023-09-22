package com.book.flaschenbook.service;

import com.book.flaschenbook.entity.BookReviewEntity;
import com.book.flaschenbook.repository.*;
import kr.co.shineware.nlp.komoran.constant.DEFAULT_MODEL;
import kr.co.shineware.nlp.komoran.core.Komoran;
import kr.co.shineware.nlp.komoran.model.KomoranResult;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
public class ReviewServiceImpl implements ReviewService {

    private final BookReviewRepository bookReviewRepository;

    @Autowired
    public ReviewServiceImpl(BookReviewRepository bookReviewRepository) {
        this.bookReviewRepository = bookReviewRepository;
    }

    public List<String> getBookReviews(String isbn) {
        List<BookReviewEntity> reviews = bookReviewRepository.findByIsbn(isbn);
        List<String> totalReviews = new ArrayList<>();

        for (BookReviewEntity review : reviews) {
            totalReviews.add(review.getContent());
        }

        String totalReviewsText = processReviewText(String.join(" ", totalReviews));
        List<String> texts = new ArrayList<>();
        texts.add(totalReviewsText);

        return texts;
    }

    public String processReviewText(String text) {
        Komoran komoran = new Komoran(DEFAULT_MODEL.LIGHT);
        KomoranResult analyzeResultList = komoran.analyze(text);
        return String.join(" ", analyzeResultList.getNouns());
    }
}