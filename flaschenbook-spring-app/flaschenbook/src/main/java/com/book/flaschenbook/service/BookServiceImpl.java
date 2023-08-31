package com.book.flaschenbook.service;

import com.book.flaschenbook.dto.BookDetailDTO;
import com.book.flaschenbook.entity.*;
import com.book.flaschenbook.model.BookModel;
import com.book.flaschenbook.repository.BookCategoryRepository;
import com.book.flaschenbook.repository.BookRepository;
import com.book.flaschenbook.repository.CodeDetailRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Subquery;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
public class BookServiceImpl implements BookService {
    private final BookRepository bookRepository;
    private ModelMapper modelMapper;
    private final BookCategoryRepository bookCategoryRepository;
    private final CodeDetailRepository codeDetailRepository;

    @Autowired
    public BookServiceImpl(BookRepository bookRepository, BookCategoryRepository bookCategoryRepository, CodeDetailRepository codeDetailRepository, ModelMapper modelMapper) {
        this.bookRepository = bookRepository;
        this.bookCategoryRepository = bookCategoryRepository;
        this.codeDetailRepository = codeDetailRepository;
        this.modelMapper = modelMapper;
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
    public List<BookModel> getNewReleasesBooks() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        Date firstDayOfMonth = calendar.getTime();
        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
        Date lastDayOfMonth = calendar.getTime();

        List<Integer> categoryIdList = codeDetailRepository.findByCommonCode(1).stream()
                .map(CodeDetailEntity::getCode).toList();
        List<BookInfoEntity> recentBooks = bookRepository.findTop20ByPubDateBetweenOrderByPubDateDesc(
                firstDayOfMonth, lastDayOfMonth
        );
        List<BookInfoEntity> filteredBooks = recentBooks.stream()
                .filter(book -> categoryIdList.contains(book.getCategoryId()))
                .toList();

        List<BookModel> books = new ArrayList<>();

        for (BookInfoEntity bookInfo : filteredBooks) {
            BookModel book = modelMapper.map(bookInfo, BookModel.class);
            for (BookDetailEntity bookDetail : bookInfo.getBookDetails()) {
                if ("AL".equals(bookDetail.getId().getWebCode())) {
                    book.setDescription(bookDetail.getDescription());
                    break;
                }
            }
            books.add(book);
        }
        return books;
    }
    @PersistenceContext
    private EntityManager entityManager;
    @Override
    @Transactional(readOnly = true)
    public List<BookModel> getBestSellers(){
        String sql ="""
                        SELECT a.*
                        FROM BookInfo a
                        JOIN BookDetail b ON a.isbn = b.isbn
                        WHERE ranking LIKE '종합%'
                        AND webCode = 'AL'
                    """;

        @SuppressWarnings("unchecked")
        List<BookInfoEntity> bestBooks = entityManager.createNativeQuery(sql, BookInfoEntity.class).getResultList();
        List<BookModel> books = new ArrayList<>();
        for (BookInfoEntity bookInfo : bestBooks) {
            BookModel book = modelMapper.map(bookInfo, BookModel.class);
            for (BookDetailEntity bookDetail : bookInfo.getBookDetails()) {
                if ("AL".equals(bookDetail.getId().getWebCode())) {
                    book.setDescription(bookDetail.getDescription());
                    book.setRanking(bookDetail.getRanking());
                    break;
                }
            }
            books.add(book);
        }
        books.sort(Comparator.comparingInt(this::extractRankingNumber));

        return books;
    }

    private int extractRankingNumber(BookModel bookModel) {
        String ranking = bookModel.getRanking();
        Pattern pattern = Pattern.compile("\\d+");
        Matcher matcher = pattern.matcher(ranking);
        if (matcher.find()) {
            return Integer.parseInt(matcher.group());
        }
        return 0;
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookModel> getBooksByHighRatingReviews() {
        String sql ="""               
                        WITH reviewCnt AS (
                        SELECT r.isbn
                             , count(r.reviewId) totalReviewCnt
                          FROM BookReview r
                         WHERE r.rating > 5
                        GROUP BY 1
                        )
                        SELECT a.*
                          FROM reviewCnt r
                          JOIN BookInfo a
                            ON r.isbn = a.isbn
                           AND a.categoryId IN (SELECT code
                                                  FROM CodeDetail
                                                 WHERE commonCode = 1)
                        ORDER BY totalReviewCnt DESC
                        LIMIT 10
                """;

        @SuppressWarnings("unchecked")
        List<BookInfoEntity> highRatingBooks = entityManager.createNativeQuery(sql, BookInfoEntity.class).getResultList();
        List<BookModel> books = new ArrayList<>();
        for (BookInfoEntity bookInfo : highRatingBooks) {
            BookModel book = modelMapper.map(bookInfo, BookModel.class);
            for (BookDetailEntity bookDetail : bookInfo.getBookDetails()) {
                if ("AL".equals(bookDetail.getId().getWebCode())) {
                    book.setDescription(bookDetail.getDescription());
                    book.setRanking(bookDetail.getRanking());
                    break;
                }
            }
            books.add(book);
        }
        return books;
    }
}
