package com.book.flaschenbook.service;

import com.book.flaschenbook.dto.BookDetailDTO;

import java.util.List;

public interface BookService {
    List<BookDetailDTO> getNewReleases();
}
