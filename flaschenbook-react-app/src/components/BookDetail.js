import React, { Component, useEffect, useState } from 'react';
import axios from 'axios';
import '../css/Detail.css'; 
import { WordCloudForDetail } from "./WordCloudForDetail";

const BookDetail = ({ book, onClose }) => {
    const [bookDetail, setBookDetail] = useState({});
    const { isbn } = book; 
    useEffect(() => {
        axios.get('/api/books/' + isbn)
          .then(response => {
            console.log('Received Book data:', response.data);
            setBookDetail(response.data);
          })
          .catch(error => {
            console.error('Error fetching Book data:', error);
          });
      }, [isbn]);

    // 출판일 포맷팅
    const rawPubDate = bookDetail.pubDate;
    const pubDateObj = new Date(rawPubDate);
    const formattedPubDate = `${pubDateObj.getFullYear()}년 ${pubDateObj.getMonth() + 1}월 ${pubDateObj.getDate()}일`;
    
    return (
        <div>
        {bookDetail ? ( // bookDetail이 null이 아닌 경우에만 렌더링합니다.
          <>
            <div className="sale-sites">
                <div className="info-item">
                    <img src={bookDetail.imageUrl} alt={bookDetail.title} />
                </div>
                <div className="info-item">
                    <h2>{bookDetail.title}</h2>
                    <p>저자: {bookDetail.author}</p>
                    <p>출판사: {bookDetail.publisher}</p>
                    <p>출판일: {formattedPubDate}</p>
                    <p>카테고리: {bookDetail.categoryName}</p>
                    <p>가격: {bookDetail.price}원</p>
                </div>
            </div>
            <div className="sale-sites">
            <div className="section-title">
                <h2>도서 구입처</h2>
                <p>로고를 클릭 시 사이트로 이동합니다.</p>
            </div>
                <div className="sale-site">
                    <a href={bookDetail.aladinSaleUrl} target="_blank" rel="noopener noreferrer">
                    <img src="/aladinlogo.png" alt="알라딘" />
                    </a>
                    <p className="info-item">가격: {bookDetail.aladinSalePrice}원</p>
                </div>
                <div className="sale-site">
                    <a href={bookDetail.naverSaleUrl} target="_blank" rel="noopener noreferrer">
                    <img src="/naver.svg" alt="네이버" />
                    </a>
                    <p className="info-item">가격: {bookDetail.naverSalePrice}원</p>
                </div>
                <div className="sale-site">
                    <a href={bookDetail.kakaoSaleUrl} target="_blank" rel="noopener noreferrer">
                    <img src="/Kakao.png" alt="카카오" />
                    </a>
                    <p className="info-item">가격: {bookDetail.kakaoSalePrice}원</p>
                </div> 
            </div>
            <div className="book-content" style={{ display: bookDetail.bookContent && bookDetail.bookContent.length > 0 ? 'block' : 'none' }}>
                <div className="section-title">
                    <h2>이 책의 한 구절</h2>
                </div>
                {bookDetail.bookContent && bookDetail.bookContent.length > 0 && (
                    bookDetail.bookContent.map((content, index) => (
                    <p key={index}>{content}</p>
                    ))
                )}
             </div>
             <div className="sale-site">
                <div className='section-title'>
                    <h2>독자들은 리뷰에서 이런 단어를 썼어요</h2>
                </div>
                <WordCloudForDetail isbn={isbn} />
             </div>
          </>
        ) : (
          <div>Loading...</div>
        )}
      </div>
    );
  };

export default BookDetail;