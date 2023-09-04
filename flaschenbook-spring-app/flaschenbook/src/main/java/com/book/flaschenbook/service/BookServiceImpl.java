package com.book.flaschenbook.service;

import com.book.flaschenbook.dto.BookDetailDTO;
import com.book.flaschenbook.entity.*;
import com.book.flaschenbook.model.BookModel;

import com.book.flaschenbook.repository.*;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class BookServiceImpl implements BookService {
    private final BookRepository bookRepository;
    private ModelMapper modelMapper;
    private final BookCategoryRepository bookCategoryRepository;

    private final BookInfoRepository bookInfoRepository;
    private final CodeDetailRepository codeDetailRepository;

    private final SurveySummaryRepository surveySummaryRepository;

    @Autowired
    public BookServiceImpl(BookRepository bookRepository, BookCategoryRepository bookCategoryRepository,
                           CodeDetailRepository codeDetailRepository, BookInfoRepository bookInfoRepository,
                           SurveySummaryRepository surveySummaryRepository,
                           ModelMapper modelMapper) {
        this.bookRepository = bookRepository;
        this.bookCategoryRepository = bookCategoryRepository;
        this.codeDetailRepository = codeDetailRepository;
        this.bookInfoRepository = bookInfoRepository;
        this.surveySummaryRepository = surveySummaryRepository;
        this.modelMapper = modelMapper;
    }
    private BookDetailDTO mapBookDetailEntityToDTO(BookDetailDTO books, List<BookDetailEntity> bookDetails) {

        for (BookDetailEntity bookDetail : bookDetails) {
            String webCode = bookDetail.getId().getWebCode();
            if ("AL".equals(webCode)) {
                books.setAladinSaleUrl(bookDetail.getSaleUrl());
                books.setAladinSalePrice(bookDetail.getSalePrice());
                books.setAladinSaleStatus(bookDetail.getSaleStatus());
                books.setAladinDescription(bookDetail.getDescription());
            } else if ("NA".equals(webCode)) {
                books.setNaverSaleUrl(bookDetail.getSaleUrl());
                books.setNaverSalePrice(bookDetail.getSalePrice());
                books.setNaverSaleStatus(bookDetail.getSaleStatus());
                books.setNaverDescription(bookDetail.getDescription());
            } else if ("KK".equals(webCode)) {
                books.setKakaoSaleUrl(bookDetail.getSaleUrl());
                books.setKakaoSalePrice(bookDetail.getSalePrice());
                books.setKakaoSaleStatus(bookDetail.getSaleStatus());
                books.setKakaoDescription(bookDetail.getDescription());
            }
        }

        return books;
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

        List<BookModel> newReleasesBooks = new ArrayList<>();

        for (BookInfoEntity bookInfo : filteredBooks) {
            BookModel book = modelMapper.map(bookInfo, BookModel.class);
            for (BookDetailEntity bookDetail : bookInfo.getBookDetails()) {
                if ("AL".equals(bookDetail.getId().getWebCode())) {
                    book.setDescription(bookDetail.getDescription());
                    break;
                }
            }
            newReleasesBooks.add(book);
        }
        return newReleasesBooks;
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
                    ORDER BY CONVERT(REGEXP_REPLACE(b.ranking, '[^0-9]+', ''), UNSIGNED)
                    LIMIT 10
                """;

        @SuppressWarnings("unchecked")
        List<BookInfoEntity> books = entityManager.createNativeQuery(sql, BookInfoEntity.class).getResultList();
        List<BookModel> bestBooks = new ArrayList<>();
        for (BookInfoEntity bookInfo : books) {
            BookModel book = modelMapper.map(bookInfo, BookModel.class);
            for (BookDetailEntity bookDetail : bookInfo.getBookDetails()) {
                if ("AL".equals(bookDetail.getId().getWebCode())) {
                    book.setDescription(bookDetail.getDescription());
                    book.setRanking(bookDetail.getRanking());
                    break;
                }
            }
            bestBooks.add(book);
        }
        bestBooks.sort(Comparator.comparingInt(this::extractRankingNumber));

        return bestBooks;
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
        List<BookInfoEntity> books = entityManager.createNativeQuery(sql, BookInfoEntity.class).getResultList();
        List<BookModel> highRatingBooks = new ArrayList<>();
        for (BookInfoEntity bookInfo : books) {
            BookModel book = modelMapper.map(bookInfo, BookModel.class);
            for (BookDetailEntity bookDetail : bookInfo.getBookDetails()) {
                if ("AL".equals(bookDetail.getId().getWebCode())) {
                    book.setDescription(bookDetail.getDescription());
                    book.setRanking(bookDetail.getRanking());
                    break;
                }
            }
            highRatingBooks.add(book);
        }
        return highRatingBooks;
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookModel> getRandomGenreBooks(){
        List<CodeDetailEntity> genreList = codeDetailRepository.findByCommonCode(2);

        List<BookInfoEntity> books = new ArrayList<>();
        String genreName = "";

        while (books.size() < 6)  {
            Random random = new Random();
            int randomIndex = random.nextInt(genreList.size());
            CodeDetailEntity genre = genreList.get(randomIndex);
            genreName = genre.getCodeName();

            String sql ="WITH reviewCnt AS (" +
                    "           SELECT r.isbn" +
                    "                , count(r.reviewId) totalReviewCnt"+
                    "             FROM BookReview r" +
                    "         GROUP BY 1)" +
                    " SELECT a.*" +
                    "  FROM BookInfo a" +
                    " JOIN BookCategory b" +
                    "   ON a.categoryId = b.categoryId" +
                    " JOIN reviewCnt c" +
                    "    ON a.isbn = c.isbn" +
                    " WHERE b." + genre.getEtc1() + " LIKE '%" + genre.getCodeName() + "%'" +
                    "   AND a.categoryId IN (SELECT code" +
                    "						  FROM CodeDetail" +
                    "						 WHERE commonCode = 1)" +
                    " ORDER BY totalReviewCnt DESC" +
                    " LIMIT 10";

            @SuppressWarnings("unchecked")
            List<BookInfoEntity> result = entityManager.createNativeQuery(sql, BookInfoEntity.class).getResultList();

            if (!result.isEmpty()) {
                books = result;
            }
        }
        List<BookModel> genreBooks = new ArrayList<>();
        for (BookInfoEntity bookInfo : books) {
            BookModel book = modelMapper.map(bookInfo, BookModel.class);
            for (BookDetailEntity bookDetail : bookInfo.getBookDetails()) {
                if ("AL".equals(bookDetail.getId().getWebCode())) {
                    book.setDescription(bookDetail.getDescription());
                    break;
                }
            }
            book.setGenre(genreName);
            genreBooks.add(book);
        }

        return genreBooks;
    }

    @Override
    @Transactional(readOnly = true)
    public BookDetailDTO getBookDetail(String isbn)
    {
        BookInfoEntity bookInfo = bookInfoRepository.findByIsbn(isbn);
        List<BookDetailEntity> bookDetails = bookInfo.getBookDetails();

        ModelMapper modelMapper = new ModelMapper();
        BookDetailDTO book = modelMapper.map(bookInfo, BookDetailDTO.class);
        book = mapBookDetailEntityToDTO(book, bookDetails);

        System.out.println(book);

        Integer categoryId = bookInfo.getCategoryId();
        Optional<BookCategoryEntity> categoryOptional = bookCategoryRepository.findByCategoryId(categoryId);
        if (categoryOptional.isPresent()) {
            BookCategoryEntity categoryEntity = categoryOptional.get();
            book.setCategoryName(categoryEntity.getDepth1() + " > " + categoryEntity.getCategoryName());
        }

        List<BookContentEntity> bookContents = bookInfo.getBookContents();
        List<String> contentList = new ArrayList<>();

        for (BookContentEntity contentEntity : bookContents) {
            String content = contentEntity.getContent();
            if (!content.matches("^[<>()\\[\\]~_\\-♣].*")) {
                contentList.add(content);
            }
        }
        List<String> selectedContent = new ArrayList<>();
        if (contentList.size() > 2)
        {
            Collections.shuffle(contentList, new Random());

            // 무작위로 두 개의 요소만 선택
            int numberOfElementsToSelect = 2;
            selectedContent = contentList.subList(0, numberOfElementsToSelect);
        }
        else {
            selectedContent = contentList;
        }

        book.setBookContent(selectedContent);

        return book;
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookModel> getRecommendationGenreBooks(Integer userId)
    {
        List<SurveySummaryEntity> surveySummaryEntites = surveySummaryRepository.findByTypeAndUserId("C", userId);
        List<CodeDetailEntity> genreList = codeDetailRepository.findByCommonCode(2);

        List<CodeDetailEntity> userGenreList = new ArrayList<>();
        for (SurveySummaryEntity surveySummary: surveySummaryEntites)
        {
            String content = surveySummary.getContent();
            for (CodeDetailEntity codeDetail : genreList) {
                String codeName = codeDetail.getCodeName();

                if (content.contains(codeName)) {
                    userGenreList.add(codeDetail);
                }
            }
        }

        String genreName = "";
        String columnName = "";

        Random random = new Random();
        List<BookInfoEntity> books = new ArrayList<>();
        while (books.isEmpty()) {
            System.out.println(userGenreList);
            if(userGenreList.isEmpty())
            {
                int randomIndex = random.nextInt(surveySummaryEntites.size());
                SurveySummaryEntity genre = surveySummaryEntites.get(randomIndex);
                genreName = genre.getContent();
                columnName = "depth2";
            }
            else
            {
                int randomIndex = random.nextInt(userGenreList.size());
                CodeDetailEntity genre = userGenreList.get(randomIndex);
                genreName = genre.getCodeName();
                columnName = genre.getEtc1();
            }
            System.out.println(genreName + "/" + columnName);

            String sql ="WITH reviewCnt AS (" +
                    "           SELECT r.isbn" +
                    "                , count(r.reviewId) totalReviewCnt"+
                    "             FROM BookReview r" +
                    "         GROUP BY 1)" +
                    " SELECT a.*" +
                    "  FROM BookInfo a" +
                    " JOIN BookCategory b" +
                    "   ON a.categoryId = b.categoryId" +
                    " JOIN reviewCnt c" +
                    "    ON a.isbn = c.isbn" +
                    " WHERE b." + columnName + " LIKE '%" + genreName + "%'" +
                    "   AND a.categoryId IN (SELECT code" +
                    "						  FROM CodeDetail" +
                    "						 WHERE commonCode = 1)" +
                    " ORDER BY totalReviewCnt DESC" +
                    " LIMIT 10";

            @SuppressWarnings("unchecked")
            List<BookInfoEntity> result = entityManager.createNativeQuery(sql, BookInfoEntity.class).getResultList();
            books = result;
        }


        List<BookModel> userGenreBooks = new ArrayList<>();
        for (BookInfoEntity bookInfo : books) {
            BookModel book = modelMapper.map(bookInfo, BookModel.class);
            for (BookDetailEntity bookDetail : bookInfo.getBookDetails()) {
                if ("AL".equals(bookDetail.getId().getWebCode())) {
                    book.setDescription(bookDetail.getDescription());
                    break;
                }
            }
            book.setGenre(genreName);
            userGenreBooks.add(book);
        }

        return userGenreBooks;
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookModel> getRelatedCustomBook(Integer userId)
    {
        List<SurveySummaryEntity> surveySummaryEntites = surveySummaryRepository.findByTypeAndUserId("S", userId);
        surveySummaryEntites.addAll(surveySummaryRepository.findByTypeAndUserId("T", userId));

        Random random = new Random();
        int randomIndex = random.nextInt(surveySummaryEntites.size());
        SurveySummaryEntity randomCustomBook = surveySummaryEntites.get(randomIndex);

        BookInfoEntity customBook = bookInfoRepository.findByIsbn(randomCustomBook.getContentId());

        String sql = "WITH MainBook AS ( " +
            "   SELECT book.categoryId category, " +
            "          cate.categoryName categoryName, " +
            "          cate.depth3 depth3, " +
            "          book.author auth, " +
            "          book.isbn " +
            "   FROM BookCategory cate " +
            "   JOIN BookInfo book ON book.categoryId = cate.categoryId " +
            "   WHERE isbn = " + customBook.getIsbn() +
            ") " +
            "SELECT a.* " +
            "FROM BookInfo a " +
            "JOIN MainBook b " +
            "WHERE a.categoryId IN ( " +
            "   SELECT c.categoryId " +
            "   FROM BookCategory c " +
            "   WHERE c.depth3 LIKE b.depth3 " +
            ") " +
            "OR a.author = b.auth " +
            "LIMIT 15";

        @SuppressWarnings("unchecked")
        List<BookInfoEntity> result = entityManager.createNativeQuery(sql, BookInfoEntity.class).getResultList();
        List<BookInfoEntity> books = result;

        List<BookModel> userReleatedBooks = new ArrayList<>();
        for (BookInfoEntity bookInfo : books) {
            BookModel book = modelMapper.map(bookInfo, BookModel.class);
            for (BookDetailEntity bookDetail : bookInfo.getBookDetails()) {
                if ("AL".equals(bookDetail.getId().getWebCode())) {
                    book.setDescription(bookDetail.getDescription());
                    break;
                }
            }
            book.setRelatedBookTitle(customBook.getTitle());
            userReleatedBooks.add(book);
        }

        return userReleatedBooks;
    }
}
