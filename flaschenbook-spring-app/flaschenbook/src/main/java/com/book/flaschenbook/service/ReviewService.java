package com.book.flaschenbook.service;

import java.util.List;

public interface ReviewService {
    List<String> getBookReviews(String isbn);
}
