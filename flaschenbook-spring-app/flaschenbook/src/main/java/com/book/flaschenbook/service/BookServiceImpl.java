package com.book.flaschenbook.service;

import com.book.flaschenbook.dto.BookDetailDTO;
import com.book.flaschenbook.dto.BookInfoDTO;
import com.book.flaschenbook.entity.BookCategoryEntity;
import com.book.flaschenbook.entity.BookDetailEntity;
import com.book.flaschenbook.entity.BookInfoEntity;
import com.book.flaschenbook.repository.BookCategoryRepository;
import com.book.flaschenbook.repository.BookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class BookServiceImpl implements BookService {

    private final BookRepository bookRepository;
    private final BookCategoryRepository bookCategoryRepository;

    @Autowired
    public BookServiceImpl(BookRepository bookRepository, BookCategoryRepository bookCategoryRepository) {
        this.bookRepository = bookRepository;
        this.bookCategoryRepository = bookCategoryRepository;
    }
    private List<BookDetailDTO> mapBookDetailEntityToDTO(List<BookInfoEntity> books) {
        List<BookDetailDTO> bookDetailDTOs = new ArrayList<>();

        for (BookInfoEntity bookInfo : books) {
            BookDetailDTO bookDetailDTO = new BookDetailDTO();
            bookDetailDTO.setIsbn(bookInfo.getIsbn());
            bookDetailDTO.setTitle(bookInfo.getTitle());
            bookDetailDTO.setCategoryId(bookInfo.getCategoryId());
            bookDetailDTO.setAuthor(bookInfo.getAuthor());
            bookDetailDTO.setTranslator(bookInfo.getTranslator());
            bookDetailDTO.setPublisher(bookInfo.getPublisher());
            bookDetailDTO.setPubDate(bookInfo.getPubDate());
            bookDetailDTO.setPrice(bookInfo.getPrice());
            bookDetailDTO.setPageCnt(bookInfo.getPageCnt());
            bookDetailDTO.setImageUrl(bookInfo.getImageUrl());
            bookDetailDTO.setRanking(bookInfo.getBookDetails().get(0).getRanking()); // Assuming ranking is same for all details

            for (BookDetailEntity bookDetail : bookInfo.getBookDetails()) {
                String webCode = bookDetail.getId().getWebCode();
                if ("AL".equals(webCode)) {
                    bookDetailDTO.setAladinSaleUrl(bookDetail.getSaleUrl());
                    bookDetailDTO.setAladinSalePrice(bookDetail.getSalePrice());
                    bookDetailDTO.setAladinSaleStatus(bookDetail.getSaleStatus());
                    bookDetailDTO.setAladinDescription(bookDetail.getDescription());
                } else if ("NA".equals(webCode)) {
                    bookDetailDTO.setNaverSaleUrl(bookDetail.getSaleUrl());
                    bookDetailDTO.setNaverSalePrice(bookDetail.getSalePrice());
                    bookDetailDTO.setNaverSaleStatus(bookDetail.getSaleStatus());
                    bookDetailDTO.setNaverDescription(bookDetail.getDescription());
                } else if ("KK".equals(webCode)) {
                    bookDetailDTO.setKakaoSaleUrl(bookDetail.getSaleUrl());
                    bookDetailDTO.setKakaoSalePrice(bookDetail.getSalePrice());
                    bookDetailDTO.setKakaoSaleStatus(bookDetail.getSaleStatus());
                    bookDetailDTO.setKakaoDescription(bookDetail.getDescription());
                }
            }

            Integer categoryId = bookInfo.getCategoryId();
            Optional<BookCategoryEntity> categoryOptional = bookCategoryRepository.findByCategoryId(categoryId);
            if (categoryOptional.isPresent()) {
                BookCategoryEntity categoryEntity = categoryOptional.get();
                bookDetailDTO.setCategoryName(categoryEntity.getDepth1() + " > " + categoryEntity.getCategoryName());
            }

            bookDetailDTOs.add(bookDetailDTO);
        }

        return bookDetailDTOs;
    }
    @Override
    @Transactional(readOnly = true)
    public List<BookDetailDTO> getNewReleases() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        Date firstDayOfMonth = calendar.getTime();
        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
        Date lastDayOfMonth = calendar.getTime();

        // 이번 달의 최신 신간 10권의 BookInfoEntity를 조회
        List<BookInfoEntity> recentBooks = bookRepository.findTop10ByPubDateBetweenOrderByPubDateDesc(
                firstDayOfMonth, lastDayOfMonth
        );

        return  mapBookDetailEntityToDTO(recentBooks);
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookInfoDTO> getNewReleasesBooks() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        Date firstDayOfMonth = calendar.getTime();
        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
        Date lastDayOfMonth = calendar.getTime();

        // 이번 달의 최신 신간 10권의 BookInfoEntity를 조회
        List<BookInfoEntity> recentBooks = bookRepository.findTop10ByPubDateBetweenOrderByPubDateDesc(
                firstDayOfMonth, lastDayOfMonth
        );

        List<BookInfoDTO> books = new ArrayList<>();

        for (BookInfoEntity bookInfo : recentBooks) {
            BookInfoDTO bookinfoDTO = new BookInfoDTO();
            bookinfoDTO.setIsbn(bookInfo.getIsbn());
            bookinfoDTO.setTitle(bookInfo.getTitle());
            bookinfoDTO.setCategoryId(bookInfo.getCategoryId());
            bookinfoDTO.setAuthor(bookInfo.getAuthor());
            bookinfoDTO.setTranslator(bookInfo.getTranslator());
            bookinfoDTO.setPublisher(bookInfo.getPublisher());
            bookinfoDTO.setPubDate(bookInfo.getPubDate());
            bookinfoDTO.setPrice(bookInfo.getPrice());
            bookinfoDTO.setPageCnt(bookInfo.getPageCnt());
            bookinfoDTO.setImageUrl(bookInfo.getImageUrl());

            books.add(bookinfoDTO);
        }

        return books;
    }
}
