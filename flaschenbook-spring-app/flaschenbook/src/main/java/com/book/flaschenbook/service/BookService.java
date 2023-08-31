package com.book.flaschenbook.service;

import com.book.flaschenbook.dto.BookDetailDTO;
import com.book.flaschenbook.dto.BookInfoDTO;
import com.book.flaschenbook.entity.BookInfoEntity;

import java.util.List;

public interface BookService {
    List<BookDetailDTO> getNewReleases();
    List<BookInfoDTO> getNewReleasesBooks();
}
