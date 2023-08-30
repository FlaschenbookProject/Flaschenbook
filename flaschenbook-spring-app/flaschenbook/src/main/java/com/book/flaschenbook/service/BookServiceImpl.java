package com.book.flaschenbook.service;

import com.book.flaschenbook.dto.BookDetailDTO;
import com.book.flaschenbook.dto.BookInfoDTO;
import com.book.flaschenbook.repository.BookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class BookServiceImpl implements BookService {

    @Autowired
    private BookRepository bookRepository;

    public List<BookDetailDTO> getBookInfo(List<BookInfoDTO> books) {
        List<BookDetailDTO> bookDetails = new ArrayList<>();

        for (BookInfoDTO bookInfoDTO : books) {
            BookDetailDTO bookDetailDTO = new BookDetailDTO();
            bookDetailDTO.setIsbn(bookInfoDTO.getIsbn());
            bookDetailDTO.setTitle(bookInfoDTO.getTitle());
            bookDetailDTO.setCategoryId(bookInfoDTO.getCategoryId());
            bookDetailDTO.setCategoryName(bookInfoDTO.getCategoryName());
            bookDetailDTO.setAuthor(bookInfoDTO.getAuthor());
            bookDetailDTO.setTranslator(bookInfoDTO.getTranslator());
            bookDetailDTO.setPublisher(bookInfoDTO.getPublisher());
            bookDetailDTO.setPubDate(bookInfoDTO.getPubDate());
            bookDetailDTO.setPrice(bookInfoDTO.getPrice());
            bookDetailDTO.setPageCnt(bookInfoDTO.getPageCnt());
            bookDetailDTO.setImageUrl(bookInfoDTO.getImageUrl());
            bookDetailDTO.setRanking(bookInfoDTO.getRanking());

            if ("AL".equals(bookInfoDTO.getWebCode())) {
                bookDetailDTO.setAladinSaleUrl(bookInfoDTO.getSaleUrl());
                bookDetailDTO.setAladinSalePrice(bookInfoDTO.getSalePrice());
                bookDetailDTO.setAladinSaleStatus(bookInfoDTO.getSaleStatus());
                bookDetailDTO.setAladinDescription(bookInfoDTO.getDescription());
            } else if ("NA".equals(bookInfoDTO.getWebCode())) {
                bookDetailDTO.setNaverSaleUrl(bookInfoDTO.getSaleUrl());
                bookDetailDTO.setNaverSalePrice(bookInfoDTO.getSalePrice());
                bookDetailDTO.setNaverSaleStatus(bookInfoDTO.getSaleStatus());
                bookDetailDTO.setNaverDescription(bookInfoDTO.getDescription());
            } else if ("KK".equals(bookInfoDTO.getWebCode())) {
                bookDetailDTO.setKakaoSaleUrl(bookInfoDTO.getSaleUrl());
                bookDetailDTO.setKakaoSalePrice(bookInfoDTO.getSalePrice());
                bookDetailDTO.setKakaoSaleStatus(bookInfoDTO.getSaleStatus());
                bookDetailDTO.setKakaoDescription(bookInfoDTO.getDescription());
            }

            bookDetails.add(bookDetailDTO);
        }

        return bookDetails;
    }

    @Override
    public List<BookDetailDTO> getNewReleases() {
        List<BookInfoDTO> newReleases = bookRepository.findNewReleases();

        return getBookInfo(newReleases);
    }
}
