import React, { Component } from 'react';
import axios from 'axios';
import Slider from 'react-slick';
import 'slick-carousel/slick/slick.css';
import 'slick-carousel/slick/slick-theme.css';
import '../css/Logo.css'; // 전역 CSS 파일을 import
import '../css/Font.css'; // Font.css 파일을 import
import '../css/Main.css'; 

class MainPage extends Component {
  state = {
    newReleases: [],
  };

  componentDidMount() {
    axios.get('/books/new_releases')
      .then(response => {
        console.log('Received new releases data:', response.data); // 데이터 확인용 로그
        this.setState({ newReleases: response.data });
      })
      .catch(error => {
        console.error('Error fetching new releases:', error);
      });
  }

  render() {
    const { newReleases } = this.state;

    // 슬라이더 설정
    const sliderSettings = {
      dots: false,
      infinite: false, // 무한 반복을 비활성화합니다.
      speed: 500,
      slidesToShow: 5,
      slidesToScroll: 3,
      initialSlide: 0,
    };
    

    // 이미지 슬라이더에 사용될 이미지 엘리먼트 생성
    const sliderImages = newReleases.map((book, index) => (
      <div key={index} className="book-slider-item">
        <img src={book.imageUrl} alt={book.title} className="book-image" />
      </div>
    ));

    return (
      <div className="main-page">
        <main>
          <section className="book-section">
            <h1 className="book-section-title">이번 주 베스트셀러</h1>
            <div className="book-slider-container">
              <Slider {...sliderSettings}>
                {sliderImages}
              </Slider>
            </div>
          </section>
          <section className="book-section">
            <h1 className="book-section-title">이달의 신간</h1>
            <div className="book-slider-container">
              <Slider {...sliderSettings}>
                {sliderImages}
              </Slider>
            </div>
          </section>
          <section className="book-section">
            <h1 className="book-section-title">#장르의 책</h1>
            <div className="book-slider-container">
              <Slider {...sliderSettings}>
                {sliderImages}
              </Slider>
            </div>
          </section>
          <section className="book-section">
            <h1 className="book-section-title">독자들이 선택한 책</h1>
            <div className="book-slider-container">
              <Slider {...sliderSettings}>
                {sliderImages}
              </Slider>
            </div>
          </section>
        </main>
      </div>
    );
  }
}


export default MainPage;