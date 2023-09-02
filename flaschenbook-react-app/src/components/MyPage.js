import React, { useState, useEffect } from "react";
import "bootstrap/dist/css/bootstrap.min.css";
import axios from "axios";
import "../css/Font.css"; // Font.css 파일을 import
import "../css/Survey.css";
import "../css/Main.css"
import { WordCloudComponent } from "./WordCloud";
import BookSliderItem from './BookSliderItem'; 
import Slider from 'react-slick';
import 'slick-carousel/slick/slick.css';
import 'slick-carousel/slick/slick-theme.css';

function MyPage() {
  const [todayBook, setTodayBook] = useState([]);
  const [relatedBooks, setRelatedBooks] = useState([]);
  const sessionInfo = JSON.parse(localStorage.getItem("sessionInfo"));
  const userId = sessionInfo.userId;
  const username = localStorage.getItem("username");

  useEffect(() => {
    const fetchData = async () => {
      try {
        const response = await axios.get("/api/my-page/today-book", {
          params: { userId },
        });
        console.log(response.data);
        setTodayBook(response.data);
      } catch (error) {
        console.error("Error fetching the book data", error);
      }
    };
    fetchData();
  }, [userId]);

  useEffect(() => {
    localStorage.setItem("todayBookIsbn", todayBook.isbn);
  }, [todayBook]);

  useEffect(() => {
    const fetchData = async () => {
      try {
        const response = await axios.get("/api/my-page/related-books", {
          params: { userId },
        });
        console.log(response.data);
        setRelatedBooks(response.data);
      } catch (error) {
        console.error("Error fetching the book data", error);
      }
    };
    fetchData();
  }, [userId]);

  const sliderSettings = {
    dots: false,
    infinite: false,
    speed: 500,
    slidesToShow: 5,
    slidesToScroll: 3,
    initialSlide: 0,
    arrows: false
  };



  const recommendationbookSlider = relatedBooks.map((book, index) => (
      <BookSliderItem key={index} book={book} />
  ));


  return (
    <div className="container">
      <div className="row">
        <div className="col-md-6 mx-auto text-center">
          <h3 className="survey-question-text">
            오늘의 도서가 파도를 타고 도착했어요.
          </h3>
          <h2 className="survey-question-text">
            "{username}" 님 오늘은 이 책 어떤가요?
          </h2>
        </div>
      </div>
      <div className="row mt-3 justify-content-center">
        <div className="col-md-3">
          <img
            src={todayBook.imageUrl}
            alt="Book Cover"
            className="img-thumbnail"
            style={{ maxWidth: "300px" }}
          />
        </div>
        <div className="col-md-5">
          <div className="container">
            <h2 className="book-title">{todayBook.title}</h2>
            <p>
              <b>카테고리</b> {todayBook.categoryName}
            </p>
            <p>
              <b>저자</b> {todayBook.author}
            </p>
            {todayBook.translator && (
              <p>
                <b>번역가</b> {todayBook.translator}
              </p>
            )}
            <h4>책 줄거리</h4>
            <p>{todayBook.kakaoDescription}</p>
            {/* <h4>구매링크</h4>
            <p>
              <a href="#">here</a>.
            </p> */}
          </div>
        </div>
      </div>
      <hr className="mt-3" style={{ display: "none" }} />
      <div className="container" style={{ marginTop: "80px" }}>
        <WordCloudComponent isbn={todayBook.isbn} />
      </div>
      <hr style={{ display: "none"  }} />
      <div className="row mt-5" style={{ marginTop: "80px" }}>
        <div className="col-md-12 text-center">
          <h3 className="survey-question-text">
            "{username}" 님 이런 도서도 있어요
          </h3>
        </div>
        <section className="book-section mt-3">
          <div className="book-slider-container">
            <Slider {...sliderSettings}>
              {recommendationbookSlider}
            </Slider>
          </div>
        </section>
      </div>
    </div>
  );
}

export default MyPage;
