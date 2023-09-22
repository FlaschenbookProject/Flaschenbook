package com.book.flaschenbook.service;

import com.book.flaschenbook.dto.BookDetailDTO;
import com.book.flaschenbook.model.BookModel;

import java.util.List;

public interface BookService {
    List<BookModel> getNewReleasesBooks();
    List<BookModel> getBestSellers();
    List<BookModel> getBooksByHighRatingReviews();
    List<BookModel> getRandomGenreBooks();
    BookDetailDTO getBookDetail(String isbn);
    List<BookModel> getRecommendationGenreBooks(Integer userId);
    List<BookModel> getRelatedCustomBook(Integer userId);
}
