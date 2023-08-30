import React, { Component } from 'react';
import axios from 'axios';
import Slider from 'react-slick';
import '../css/Logo.css'; // 전역 CSS 파일을 import
import '../css/Font.css'; // Font.css 파일을 import
import '../css/Main.css'; 

class MainPage extends Component {
    state = {
      isLoggedIn: false,
      userGenre: '',
      userKeywords: '',
      bestSellers: [],
      newReleases: [], // 새로운 도서 목록을 저장할 상태
      trendingBooks: [],
      popularBooks: [],
    };

    componentDidMount() {
      // 스프링에서 데이터를 가져오는 API 요청을 보냅니다.
      axios.get('/books/new_releases')
        .then(response => {
          // API 응답에서 새로운 도서 목록을 추출하여 상태에 설정합니다.
          this.setState({ newReleases: response.data });
        })
        .catch(error => {
          console.error('API 호출 중 오류 발생:', error);
        });
    }

    render() {
      const { newReleases } = this.state;

      const settings = {
        dots: true,
        infinite: true,
        speed: 500,
        slidesToShow: 5,
        slidesToScroll: 1,
      };
      
    return (
      <div className="main-page">
        <main>
          <section className="book-section">
            <h1 className="book-section-title">이번 달 신간</h1>
            <Slider class="book-slider"{...settings}>
              {newReleases.map(book => (
                <div key={book.id}>
                  <img src={book.imgUrl} alt={book.title} />
                  <p>{book.title}</p>
                </div>
              ))}
            </Slider>
          </section>
        </main>
      </div>
    );
  }
}

export default MainPage;
