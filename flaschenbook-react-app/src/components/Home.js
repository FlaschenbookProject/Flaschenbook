import React, { useState, useEffect, useRef, useContext } from 'react';
import {
  MDBBtn,
  MDBModal,
  MDBModalDialog,
  MDBModalContent,
  MDBModalHeader,
  MDBModalTitle,
  MDBModalBody,
  MDBModalFooter,
} from 'mdb-react-ui-kit';
import axios from 'axios';
import BookDetail from './BookDetail'; 
import Slider from 'react-slick';
import 'slick-carousel/slick/slick.css';
import 'slick-carousel/slick/slick-theme.css';
import BookSliderItem from './BookSliderItem'; 
import AppContext from "../context";
import '../css/Logo.css'; // 전역 CSS 파일을 import
import '../css/Font.css'; // Font.css 파일을 import
import '../css/Main.css'; 

function MainPage() {
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [selectedBook, setSelectedBook] = useState(null);
  const [newReleases, setNewReleases] = useState([]);
  const [bestSellers, setBestSellers] = useState([]);
  const [highRatingBooks, setHighRatingBooks] = useState([]);
  const [randomGenreBooks, setRandomGenreBooks] = useState([]);
  const [recommendationGenreBooks, setRecommendationGenreBooks] = useState([]);
  const [customRelatedBooks, setCustomRelatedBooks] = useState([]);
  const [firstGenre, setFirstGenre] = useState('');
  const [customGenre, setCustomGenre] = useState('');
  const [customBook, setCustomBook] = useState('');
  const [basicModal, setBasicModal] = useState(false);
  const hiddenBtnRef = useRef(null);
  const { setIsLogged, isLogged } = useContext(AppContext);
  const sessionInfo = JSON.parse(localStorage.getItem("sessionInfo"));
  const userId = sessionInfo ? sessionInfo.userId : null;
  const username = localStorage.getItem("username");

  const toggleShow = () => setBasicModal(!basicModal);

  const openModal = (book) => {
    setSelectedBook(book);
    setIsModalOpen(true);
    hiddenBtnRef.current.click();
  };

  const closeModal = () => {
    setSelectedBook(null);
    setIsModalOpen(false);
  };

  useEffect(() => {
    axios.get('/api/books/new_book_info')
      .then(response => {
        console.log('Received new releases data:', response.data);
        setNewReleases(response.data);
      })
      .catch(error => {
        console.error('Error fetching new releases:', error);
      });

    axios.get('/api/books/best_sellers')
      .then(response => {
        console.log('Received best sellers data:', response.data);
        setBestSellers(response.data);
      })
      .catch(error => {
        console.error('Error fetching best sellers:', error);
      });

    axios.get('/api/books/high_rating_books')
      .then(response => {
        console.log('Received high rating books data:', response.data);
        setHighRatingBooks(response.data);
      })
      .catch(error => {
        console.error('Error fetching high rating books:', error);
      });

    axios.get('/api/books/genre_books')
      .then(response => {
        console.log('Received genre books data:', response.data);
        const firstGenre = response.data[0].genre;
        setRandomGenreBooks(response.data);
        setFirstGenre(firstGenre);
      })
      .catch(error => {
        console.error('Error fetching genre books:', error);
      });
    axios.get(`/api/books/rc_genre_books?userId=${userId}`)
      .then(response => {
        console.log('Received User genre books data:', response.data);
        const customGenre = response.data[0].genre;
        setRecommendationGenreBooks(response.data);
        setCustomGenre(customGenre);
      })
      .catch(error => {
        console.error('Error fetching User genre books:', error);
      });
 
      axios.get(`/api/books/rc_books?userId=${userId}`)
      .then(response => {
        console.log('Received User Related books data:', response.data);
        const customBook = response.data[0].relatedBookTitle;
        setCustomRelatedBooks(response.data);
        setCustomBook(customBook);
      })
      .catch(error => {
        console.error('Error fetching User Related books:', error);
      });     
  }, []);

  const handleLogout = () => {
    const sessionInfo = JSON.parse(localStorage.getItem("sessionInfo"));
    fetch("/api/users/logout", {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify(sessionInfo),
    })
      .then((response) => response.text())
      .then((data) => {
        console.log("Success:", data);
        localStorage.removeItem("sessionInfo");
        setIsLogged(false);
        window.location.href = "/";
      })
      .catch((error) => {
        console.error("Error:", error);
      });
  };

  // 슬라이더 설정
  const sliderSettings = {
    dots: false,
    infinite: false,
    speed: 500,
    slidesToShow: 5,
    slidesToScroll: 3,
    initialSlide: 0,
    arrows: false
  };

  const newSliderImages = newReleases.map((book, index) => (
    <div key={index} onClick={() => openModal(book)}>
      <BookSliderItem key={index} book={book} />
    </div>
  ));

  const bestSliderImages = bestSellers.map((book, index) => (
    <div key={index} onClick={() => openModal(book)}>
      <BookSliderItem key={index} book={book} />
    </div>
  ));

  const highRatingSliderImages = highRatingBooks.map((book, index) => (
    <div key={index} onClick={() => openModal(book)}>
      <BookSliderItem key={index} book={book} />
    </div>
  ));

  const randomGenreSliderImages = randomGenreBooks.map((book, index) => (
    <div key={index} onClick={() => openModal(book)}>
      <BookSliderItem key={index} book={book} />
    </div>
  ));

  const randomUserGenreSliderImages = recommendationGenreBooks.map((book, index) => (
    <div key={index} onClick={() => openModal(book)}>
      <BookSliderItem key={index} book={book} />
    </div>
  ));
 
  const randomCustomRelatedBookSliderImages = customRelatedBooks.map((book, index) => (
    <div key={index} onClick={() => openModal(book)}>
      <BookSliderItem key={index} book={book} />
    </div>
  ));

  return (
    <div className="main-page">
      <main>
      {userId && isLogged && (
        <>
          <section className="book-section">
            <h1 className="book-section-title">{username} 님을 위한 #{customGenre} 책</h1>
            <div className="book-slider-container">
              <Slider {...sliderSettings}>
                {randomUserGenreSliderImages}
              </Slider>
            </div>
          </section>
          <section className="book-section">
            <h1 className="book-section-title">
              {customBook} 유사한 책
            </h1>
            <div className="book-slider-container">
              <Slider {...sliderSettings}>
                {randomCustomRelatedBookSliderImages}
              </Slider>
            </div>
          </section>
        </>
      )}
        <section className="book-section">
          <h1 className="book-section-title">이번 주 베스트셀러</h1>
          <div className="book-slider-container">
            <Slider {...sliderSettings}>
              {bestSliderImages}
            </Slider>
          </div>
        </section>
        <section className="book-section">
          <h1 className="book-section-title">이달의 신간</h1>
          <div className="book-slider-container">
            <Slider {...sliderSettings}>
              {newSliderImages}
            </Slider>
          </div>
        </section>
        {!isLogged && (
              <>
              <section className="book-section">
                <h1 className="book-section-title">독자들이 선택한 책</h1>
                <div className="book-slider-container">
                  <Slider {...sliderSettings}>
                    {highRatingSliderImages}
                  </Slider>
                </div>
              </section>
              <section className="book-section">
                <h1 className="book-section-title">#오늘의 책 #{firstGenre}</h1>
                <div className="book-slider-container">
                  <Slider {...sliderSettings}>
                    {randomGenreSliderImages}
                  </Slider>
                </div>
              </section>
              </>
            )}
        <MDBBtn ref={hiddenBtnRef} style={{ display: 'none' }} onClick={toggleShow}>LAUNCH DEMO MODAL</MDBBtn>
        <MDBModal show={basicModal} setShow={setBasicModal} tabIndex="-1" dialogClassName="modal-dialog-centered" >
        <MDBModalDialog>
          <MDBModalContent>
            <MDBModalBody>  {selectedBook && (
                              <div>
                                <BookDetail book={selectedBook} onClose={closeModal}/>
                              </div>
                            )}
            </MDBModalBody>
          </MDBModalContent>
        </MDBModalDialog>
      </MDBModal>
      </main>
    </div>
  );
}

export default MainPage;

