package com.book.flaschenbook.service;


import com.book.flaschenbook.dto.BookDetailDTO;
import com.book.flaschenbook.model.BookModel;

import java.util.List;

public interface MyPageService {
    BookModel getTodayBook(int userId);
    List<BookModel> getRelatedBooks(int userId);
    BookDetailDTO getBookDetail(BookModel bookModel);
}
