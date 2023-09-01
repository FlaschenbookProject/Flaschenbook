package com.book.flaschenbook.service;

import com.book.flaschenbook.dto.BookDetailDTO;
import com.book.flaschenbook.entity.*;
import com.book.flaschenbook.model.BookModel;
import com.book.flaschenbook.model.SurveyContentModel;
import com.book.flaschenbook.repository.*;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class MyPageServiceImpl implements MyPageService{
    private final SurveysRepository surveysRepository;
    private final SurveyDetailsRepository surveyDetailsRepository;
    private final BookInfoRepository bookInfoRepository;
    private final BookContentRepository bookContentRepository;
    private final RecommendedBooksRepository recommendedBooksRepository;
    private final ModelMapper modelMapper;
    private final SurveySummaryRepository surveySummaryRepository;
    private final BookDetailRepository bookDetailRepository;

    @Autowired
    public MyPageServiceImpl(SurveysRepository surveysRepository, SurveyDetailsRepository surveyDetailsRepository, BookInfoRepository bookInfoRepository, BookContentRepository bookContentRepository, RecommendedBooksRepository recommendedBooksRepository, ModelMapper modelMapper, SurveySummaryRepository surveySummaryRepository, BookDetailRepository bookDetailRepository) {
        this.surveysRepository = surveysRepository;
        this.surveyDetailsRepository = surveyDetailsRepository;
        this.bookInfoRepository = bookInfoRepository;
        this.bookContentRepository = bookContentRepository;
        this.recommendedBooksRepository = recommendedBooksRepository;
        this.modelMapper = modelMapper;
        this.surveySummaryRepository = surveySummaryRepository;
        this.bookDetailRepository = bookDetailRepository;
    }
    Date today = Date.valueOf(LocalDate.now());

    @Override
    public BookModel getTodayBook(int userId) {
        BookInfoEntity recommendedBook = null;

        RecommendedBooksEntity recommendedBooksEntity = recommendedBooksRepository.findByUserIdAndRecommendDate(userId, today);
        if (recommendedBooksEntity != null){
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


        return null;
    }


    @Override
    public List<BookModel> getRelatedBooks(int userId){
        List<SurveySummaryEntity> surveySummaries = surveySummaryRepository.findByTypeAndUserId("C", userId);
        List<Integer> newCategoryIds = surveySummaries.stream()
                .map(summary -> Integer.parseInt(summary.getContentId()))
                .collect(Collectors.toList());

        List<BookInfoEntity> relatedBooks = bookInfoRepository.findByNewCategoryIds(newCategoryIds);
        return relatedBooks.stream()
                .sorted(Comparator.comparing(BookInfoEntity::getPubDate).reversed())
                .limit(5)
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

}
