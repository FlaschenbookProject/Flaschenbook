package com.book.flaschenbook.controller;

import com.book.flaschenbook.dto.BookDetailDTO;
import com.book.flaschenbook.dto.BookInfoDTO;
import com.book.flaschenbook.entity.BookInfoEntity;
import com.book.flaschenbook.service.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/books")
public class MainController {
    @Autowired
    private BookService bookService;

    @Autowired
    public MainController(BookService bookService) {
        this.bookService = bookService;
    }


/*
    @GetMapping("/best_sellers")
    public ResponseEntity<List<BookInfoModel>> getBestsellers() {
        List<BookInfoModel> bestsellers = bookService.getBestsellers();
        return ResponseEntity.ok(bestsellers);
    }
*/
    @GetMapping("/new_releases")
    public ResponseEntity<List<BookDetailDTO>> getNewReleases() {
        List<BookDetailDTO> newReleases = bookService.getNewReleases();
        return ResponseEntity.ok(newReleases);
    }

    @GetMapping("/new_book_info")
    public ResponseEntity<List<BookInfoDTO>> getNewReleasesTest() {
        List<BookInfoDTO> newReleasesTest = bookService.getNewReleasesBooks();
        return ResponseEntity.ok(newReleasesTest);
    }
/*
    @GetMapping("/genre_books")
    public ResponseEntity<List<BookInfoModel>> getGenreBooks() {
        List<BookInfoModel> genreBooks = bookService.getGenreBooks();
        return ResponseEntity.ok(genreBooks);
    }

    @GetMapping("/most_reviewed_books")
    public ResponseEntity<List<BookInfoModel>> getMostReviewedBooks() {
        List<BookInfoModel> mostReviewedBooks = bookService.getMostReviewedBooks();
        return ResponseEntity.ok(mostReviewedBooks);
    }

    @GetMapping("/rc_genre_books")
    public ResponseEntity<List<BookInfoModel>> getRecommendationGenreBooks() {
        List<BookInfoModel> recommendationGenreBooks = bookService.getRecommendationGenreBooks();
        return ResponseEntity.ok(recommendationGenreBooks);
    }

    @GetMapping("/rc_author_books")
    public ResponseEntity<List<BookInfoModel>> getRecommendationAuthorBooks() {
        List<BookInfoModel> recommendationAuthorBooks = bookService.getRecommendationAuthorBooks();
        return ResponseEntity.ok(recommendationAuthorBooks);
    }

    @GetMapping("/rc_books")
    public ResponseEntity<List<BookInfoModel>> getRecommendationBooks() {
        List<BookInfoModel> recommendationBooks = bookService.getRecommendationBooks();
        return ResponseEntity.ok(recommendationBooks);
    }*/
}