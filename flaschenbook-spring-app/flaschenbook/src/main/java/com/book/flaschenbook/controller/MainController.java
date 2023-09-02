package com.book.flaschenbook.controller;

import com.book.flaschenbook.dto.BookDetailDTO;
import com.book.flaschenbook.dto.BookInfoDTO;
import com.book.flaschenbook.entity.BookInfoEntity;
import com.book.flaschenbook.model.BookModel;

import com.book.flaschenbook.service.BookService;
import com.book.flaschenbook.service.ReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/books")
public class MainController {
    @Autowired
    private BookService bookService;
    @Autowired
    private ReviewService reviewService;

    @Autowired
    public MainController(BookService bookService, ReviewService reviewService) {
        this.bookService = bookService;
        this.reviewService = reviewService;
    }


    @GetMapping("/best_sellers")
    public ResponseEntity<List<BookModel>> getBestsellers() {
        List<BookModel> bestsellers = bookService.getBestSellers();
        return ResponseEntity.ok(bestsellers);
    }
    @GetMapping("/new_book_info")
    public ResponseEntity<List<BookModel>> getNewReleasesTest() {
        List<BookModel> newReleasesTest = bookService.getNewReleasesBooks();

        return ResponseEntity.ok(newReleasesTest);
    }

    @GetMapping("/genre_books")
    public ResponseEntity<List<BookModel>> getGenreBooks() {
        List<BookModel> genreBooks = bookService.getRandomGenreBooks();
        return ResponseEntity.ok(genreBooks);
    }

    @GetMapping("/high_rating_books")
    public ResponseEntity<List<BookModel>> getMostReviewedBooks() {
        List<BookModel> mostReviewedBooks = bookService.getBooksByHighRatingReviews();
        return ResponseEntity.ok(mostReviewedBooks);
    }

    @GetMapping("/{isbn}")
    public ResponseEntity<BookDetailDTO> getBookInfo(@PathVariable String isbn) {
        BookDetailDTO bookDetail = bookService.getBookDetail(isbn);
        if (bookDetail != null) {
            return new ResponseEntity<>(bookDetail, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/rc_genre_books")
    public ResponseEntity<List<BookModel>> getRecommendationGenreBooks(@RequestParam int userId) {
        List<BookModel> recommendationGenreBooks = bookService.getRecommendationGenreBooks(userId);
        return ResponseEntity.ok(recommendationGenreBooks);
    }


    @GetMapping("/rc_books")
    public ResponseEntity<List<BookModel>> getRecommendationRelatedBook(@RequestParam int userId) {
        List<BookModel> recommendationRelatedBook = bookService.getRelatedCustomBook(userId);
        return ResponseEntity.ok(recommendationRelatedBook);
    }

    @GetMapping("/word_cloud_detail")
    public ResponseEntity<List<String>> getWordCloudBooks(@RequestParam String isbn) {
        System.out.println(isbn);
        List<String> wordCloudForDetail = reviewService.getBookReviews(isbn);
        return ResponseEntity.ok(wordCloudForDetail);
    }

}