package com.book.flaschenbook.service;

import com.book.flaschenbook.dto.BookDetailDTO;
import com.book.flaschenbook.entity.*;
import com.book.flaschenbook.model.BookModel;
import com.book.flaschenbook.model.SurveyContentModel;
import com.book.flaschenbook.repository.*;
import kr.co.shineware.nlp.komoran.constant.DEFAULT_MODEL;
import kr.co.shineware.nlp.komoran.core.Komoran;
import kr.co.shineware.nlp.komoran.model.KomoranResult;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.sql.Date;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;


@Service
public class MyPageServiceImpl implements MyPageService {
    private final SurveysRepository surveysRepository;
    private final SurveyDetailsRepository surveyDetailsRepository;
    private final BookInfoRepository bookInfoRepository;
    private final BookContentRepository bookContentRepository;
    private final RecommendedBooksRepository recommendedBooksRepository;
    private final ModelMapper modelMapper;
    private final SurveySummaryRepository surveySummaryRepository;
    private final BookDetailRepository bookDetailRepository;
    private final BookCategoryRepository bookCategoryRepository;
    private final BookReviewRepository bookReviewRepository;
    private final NewBookCategoryRepository newBookCategoryRepository;

    @Autowired
    public MyPageServiceImpl(SurveysRepository surveysRepository, SurveyDetailsRepository surveyDetailsRepository, BookInfoRepository bookInfoRepository, BookContentRepository bookContentRepository, RecommendedBooksRepository recommendedBooksRepository, ModelMapper modelMapper, SurveySummaryRepository surveySummaryRepository, BookDetailRepository bookDetailRepository, BookCategoryRepository bookCategoryRepository, BookReviewRepository bookReviewRepository, NewBookCategoryRepository newBookCategoryRepository) {
        this.surveysRepository = surveysRepository;
        this.surveyDetailsRepository = surveyDetailsRepository;
        this.bookInfoRepository = bookInfoRepository;
        this.bookContentRepository = bookContentRepository;
        this.recommendedBooksRepository = recommendedBooksRepository;
        this.modelMapper = modelMapper;
        this.surveySummaryRepository = surveySummaryRepository;
        this.bookDetailRepository = bookDetailRepository;
        this.bookCategoryRepository = bookCategoryRepository;
        this.bookReviewRepository = bookReviewRepository;
        this.newBookCategoryRepository = newBookCategoryRepository;
    }

    Date today = Date.valueOf(LocalDate.now());

    @Override
    public BookModel getTodayBook(int userId) {
        BookInfoEntity recommendedBook;

        RecommendedBooksEntity recommendedBooksEntity = recommendedBooksRepository.findByUserIdAndRecommendDate(userId, today);
        if (recommendedBooksEntity != null) {
            recommendedBook = bookInfoRepository.findByIsbn(recommendedBooksEntity.getIsbn());
            return modelMapper.map(recommendedBook, BookModel.class);
        }

        // 사용자의 선호 데이터 가져오기
        List<SurveyContentModel> prefContentList = findPrefContentsByUserId(userId);

        // 책 추천 로직 구현해야 함

        //  간단하게 사용자가 선택한 책제목의 isbn 하나를 골라서 그 책을 반환함
        Optional<SurveyContentModel> optionalContent = prefContentList.stream()
                .filter(content -> "T".equals(content.getType()))
                .findAny();

        String isbn = null;
        if (optionalContent.isPresent()) {
            isbn = optionalContent.get().getId();
        }

        recommendedBook = bookInfoRepository.findByIsbn(isbn);
        saveRecommendedBook(userId, recommendedBook);

        return modelMapper.map(recommendedBook, BookModel.class);
    }

    @Override
    public BookDetailDTO getBookDetail(BookModel bookModel) {
        BookDetailDTO bookDetailDTO = modelMapper.map(bookModel, BookDetailDTO.class);
        Optional<BookCategoryEntity> categoryEntity = bookCategoryRepository.findByCategoryId(bookModel.getCategoryId());
        categoryEntity.ifPresent(bookCategoryEntity -> bookDetailDTO.setCategoryName(bookCategoryEntity.getCategoryName()));
        Optional<BookDetailEntity> bookDetailEntityAL = bookDetailRepository.findById(new BookDetailIdEntity(bookDetailDTO.getIsbn(), "AL"));
        Optional<BookDetailEntity> bookDetailEntityNA = bookDetailRepository.findById(new BookDetailIdEntity(bookDetailDTO.getIsbn(), "NA"));
        Optional<BookDetailEntity> bookDetailEntityKK = bookDetailRepository.findById(new BookDetailIdEntity(bookDetailDTO.getIsbn(), "KK"));

        bookDetailEntityAL.ifPresent(bookDetailEntity -> {
            bookDetailDTO.setRanking(bookDetailEntityAL.get().getRanking());
            bookDetailDTO.setAladinDescription(bookDetailEntityAL.get().getDescription());
            bookDetailDTO.setAladinSaleUrl(bookDetailEntityAL.get().getSaleUrl());
            bookDetailDTO.setAladinSalePrice(bookDetailEntityAL.get().getSalePrice());
        });

        bookDetailEntityNA.ifPresent(bookDetailEntity -> {
            bookDetailDTO.setNaverDescription(bookDetailEntityNA.get().getDescription());
            bookDetailDTO.setNaverSaleUrl(bookDetailEntityNA.get().getSaleUrl());
            bookDetailDTO.setNaverSalePrice(bookDetailEntityNA.get().getSalePrice());
        });

        bookDetailEntityKK.ifPresent(bookDetailEntity -> {
            bookDetailDTO.setKakaoDescription(bookDetailEntityKK.get().getDescription());
            bookDetailDTO.setKakaoSaleUrl(bookDetailEntityKK.get().getSaleUrl());
            bookDetailDTO.setKakaoSalePrice(bookDetailEntityKK.get().getSalePrice());
        });

        return bookDetailDTO;
    }


    @Override
    public List<BookModel> getRelatedBooks(int userId) {
        List<SurveySummaryEntity> surveySummaries = surveySummaryRepository.findByTypeAndUserId("C", userId);
        List<Integer> newCategoryIds = surveySummaries.stream()
                .map(summary -> Integer.parseInt(summary.getContentId()))
                .collect(Collectors.toList());

        List<BookInfoEntity> relatedBooks = bookInfoRepository.findByNewCategoryIds(newCategoryIds);

        // Shuffle the list
        Collections.shuffle(relatedBooks);

        return relatedBooks.stream()
                .limit(6)
                .map(book -> modelMapper.map(book, BookModel.class)).toList();
    }

    public List<SurveyContentModel> findPrefContentsByUserId(Integer userId) {
        List<SurveysEntity> surveys = surveysRepository.findByUserId(userId);
        List<SurveyContentModel> contents = new ArrayList<>();

        for (SurveysEntity survey : surveys) {
            List<SurveyDetailsEntity> surveyDetails = surveyDetailsRepository.findBySurveyId(survey.getSurveyId());

            for (SurveyDetailsEntity surveyDetail : surveyDetails) {
                String contentId = surveyDetail.getContentId();
                String contentType = surveyDetail.getContentType();

                SurveyContentModel surveyContentModel = new SurveyContentModel();
                surveyContentModel.setId(contentId);
                surveyContentModel.setType(contentType);
                switch (contentType) {
                    case "T" -> {
                        BookInfoEntity bookInfo = bookInfoRepository.findByIsbn(contentId);
                        if (bookInfo != null) {
                            surveyContentModel.setContent(bookInfo.getTitle());
                        }
                    }
                    case "S" -> {
                        Optional<BookContentEntity> bookContent = bookContentRepository.findById(Integer.parseInt(contentId));
                        bookContent.ifPresent(bookContentEntity -> {
                            surveyContentModel.setId(bookContentEntity.getIsbn());
                            surveyContentModel.setContent(bookContentEntity.getContent());
                        });

                    }
                    case "C" -> {
                        Optional<NewBookCategoryEntity> bookCategory = newBookCategoryRepository.findById(Integer.parseInt(contentId));
                        bookCategory.ifPresent(bookCategoryEntity -> {
                            surveyContentModel.setId(String.valueOf(bookCategoryEntity.getCategoryId()));
                            surveyContentModel.setContent(bookCategoryEntity.getCategoryName());
                        });
                    }
                }
                contents.add(surveyContentModel);
            }
        }
        return contents;
    }

    public List<RecommendedBooksEntity> getRecentRecommendedBooks(int userId) {
        Pageable pageable = PageRequest.of(0, 5, Sort.by("recommendDate").descending());
        Page<RecommendedBooksEntity> page = recommendedBooksRepository.findByUserId(userId, pageable);
        return page.getContent();
    }

    public void saveRecommendedBook(int userId, BookInfoEntity book) {
        RecommendedBooksEntity recommendedBooksEntity = new RecommendedBooksEntity();
        recommendedBooksEntity.setUserId(userId);
        recommendedBooksEntity.setIsbn(book.getIsbn());
        recommendedBooksEntity.setRecommendDate(today);
        recommendedBooksRepository.save(recommendedBooksEntity);
    }

    public List<String> getBookReviews(String isbn) {
        List<BookReviewEntity> reviews = bookReviewRepository.findByIsbn(isbn);
        return classifyReviews(reviews);
    }

    public List<String> classifyReviews(List<BookReviewEntity> reviews) {
        List<String> positiveReviews = new ArrayList<>();
        List<String> negativeReviews = new ArrayList<>();

        for (BookReviewEntity review : reviews) {
            BigDecimal rating = review.getRating();
            if (rating.compareTo(BigDecimal.valueOf(7)) >= 0) {
                positiveReviews.add(review.getContent());
            } else if (rating.compareTo(BigDecimal.valueOf(6)) <= 0) {
                negativeReviews.add(review.getContent());
            }
        }
        String positiveReviewsText = processReviewText(String.join(" ", positiveReviews));
        String negativeReviewsText = processReviewText(String.join(" ", negativeReviews));
        List<String> texts = new ArrayList<>();
        texts.add(positiveReviewsText);
        texts.add(negativeReviewsText);

        return texts;
    }

    public String processReviewText(String text) {
        Komoran komoran = new Komoran(DEFAULT_MODEL.LIGHT);
        KomoranResult analyzeResultList = komoran.analyze(text);
        return String.join(" ", analyzeResultList.getNouns());
    }

}
