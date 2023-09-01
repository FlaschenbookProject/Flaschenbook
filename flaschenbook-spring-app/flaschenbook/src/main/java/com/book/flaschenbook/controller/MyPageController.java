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

import java.util.List;

@RestController
@RequestMapping("/my-page")
public class MyPageController {
    private final MyPageService myPageService;
    private final ModelMapper modelMapper;

    public MyPageController(MyPageService myPageService, ModelMapper modelMapper) {
        this.myPageService = myPageService;
        this.modelMapper = modelMapper;
    }

    @GetMapping("/today-book")
    public ResponseEntity<BookDetailDTO> getTodayBook(@RequestParam int userId) {
        BookModel bookModel = myPageService.getTodayBook(userId);
        BookDetailDTO

        return ResponseEntity.ok(bookModel);
    }

    @GetMapping("/related-books")
    public ResponseEntity<List<BookModel>> getRecommendedBooks(@RequestParam int userId) {
        List<BookModel> relatedBooks = myPageService.getRelatedBooks(userId);
        return ResponseEntity.ok(relatedBooks);
    }
}
