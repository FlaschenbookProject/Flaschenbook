import React, { Component } from 'react';
import axios from 'axios';
import Slider from 'react-slick';
import MainLogo from './MainLogo';
import Footer from './Footer';
import '../css/Logo.css'; // 전역 CSS 파일을 import
import '../css/Font.css'; // Font.css 파일을 import

class MainPage extends Component {
  state = {
    isLoggedIn: false,
    userGenre: '',
    userKeywords: '',
    bestSellers: [],
    newReleases: [],
    trendingBooks: [],
    popularBooks: [],
  };

  componentDidMount() {
    // 서버에서 사용자 정보 및 도서 데이터 가져오기
    axios.get('/api/userInfo')
      .then(response => {
        const { isLoggedIn, genre, keywords} = response.data;
        this.setState({ isLoggedIn, userGenre: genre, userKeywords: keywords });
      })
      .catch(error => {
        console.error('Error fetching user info:', error);
      });

    axios.get('/api/bestSellers')
      .then(response => {
        this.setState({ bestSellers: response.data });
      })
      .catch(error => {
        console.error('Error fetching best sellers:', error);
      });

    axios.get('/api/newReleases')
      .then(response => {
        this.setState({ newReleases: response.data });
      })
      .catch(error => {
        console.error('Error fetching new releases:', error);
      });

    axios.get('/api/trendingBooks')
      .then(response => {
        this.setState({ trendingBooks: response.data });
      })
      .catch(error => {
        console.error('Error fetching trending books:', error);
      });

    axios.get('/api/popularBooks')
      .then(response => {
        this.setState({ popularBooks: response.data });
      })
      .catch(error => {
        console.error('Error fetching popular books:', error);
      });
  }

  render() {
    const {
      isLoggedIn,
      userGenre,
      userKeywords,
      bestSellers,
      newReleases,
      trendingBooks,
      popularBooks,
    } = this.state;

    const settings = {
      dots: true,
      infinite: true,
      speed: 500,
      slidesToShow: 5,
      slidesToScroll: 1,
    };

    return (
      <div className="main-page">
        <header>
          <div className="logo">
            <MainLogo />
            {/* 로고 이미지 */}
          </div>
        </header>
        <main>
          {isLoggedIn && (
            <>
              <section>
                <h1>{`" 님이 선호하는 ${userGenre}" 장르의 책`}</h1>
                <Slider {...settings}>
                  {bestSellers.map(book => (
                    <div key={book.id}>
                      <img src={book.imgUrl} alt={book.title} />
                      <p>{book.title}</p>
                    </div>
                  ))}
                </Slider>
              </section>
              <section>
                <h1>{`" 님을 위한 책`}</h1>
                <Slider {...settings}>
                  {bestSellers.map(book => (
                    <div key={book.id}>
                      <img src={book.thumbnailUrl} alt={book.title} />
                      <p>{book.title}</p>
                    </div>
                  ))}
                </Slider>
              </section>
            </>
          )}

          <section>
            <h1>이번 주의 베스트 셀러</h1>
            <Slider {...settings}>
              {bestSellers.map(book => (
                <div key={book.id}>
                  <img src={book.imgUrl} alt={book.title} />
                  <p>{book.title}</p>
                </div>
              ))}
            </Slider>
          </section>
          <section>
            <h1>이달의 신간</h1>
            <Slider {...settings}>
              {newReleases.map(book => (
                <div key={book.id}>
                  <img src={book.imgUrl} alt={book.title} />
                  <p>{book.title}</p>
                </div>
              ))}
            </Slider>
          </section>
        </main>
        <div className="footer">
            <Footer />
          </div>
      </div>
    );
  }
}

export default MainPage;
