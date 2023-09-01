package com.book.flaschenbook.controller;

import com.book.flaschenbook.dto.BookDetailDTO;
import com.book.flaschenbook.model.BookModel;
import com.book.flaschenbook.service.MyPageService;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/my-page")
public class MyPageController {
    private final MyPageService myPageService;

    public MyPageController(MyPageService myPageService) {
        this.myPageService = myPageService;
    }

    @GetMapping("/today-book")
    public ResponseEntity<BookDetailDTO> getTodayBook(@RequestParam int userId) {
        BookModel bookModel = myPageService.getTodayBook(userId);
        BookDetailDTO todayBook = myPageService.getBookDetail(bookModel);
        return ResponseEntity.ok(todayBook);
    }

    @GetMapping("/related-books")
    public ResponseEntity<List<BookModel>> getRecommendedBooks(@RequestParam int userId) {
        List<BookModel> relatedBooks = myPageService.getRelatedBooks(userId);
        return ResponseEntity.ok(relatedBooks);
    }

    @GetMapping("/book-words")
    public ResponseEntity<List<String>> getWordCloudSourceText(@RequestParam String isbn) {
        List<String> textList = myPageService.getReviewTexts();
        if (textList != null){
            return ResponseEntity.ok(textList);
        }
        try{
            myPageService.getBookReviews(isbn);
        }
        catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(null);
        }
        return ResponseEntity.ok(myPageService.getReviewTexts());
    }
}
