import React, { useState, useEffect } from "react";
import "bootstrap/dist/css/bootstrap.min.css";
import axios from "axios";
import "../css/Font.css"; // Font.css 파일을 import
import "../css/Survey.css";

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

  return (
    <div className="container mt-5">
      <div className="row">
        <div className="col-md-6 mx-auto text-center">
          <h3 className="survey-question-text">
            오늘의 도서가 파도를 타고 도착했어요.
          </h3>
          <h2 className="survey-question-text">
            "{username}" 님 오늘은 이 책 어떤가요?
          </h2>
          <img
            src={todayBook.imageUrl}
            alt="Book Cover"
            className="img-thumbnail"
          />
          <h2>{todayBook.title}</h2>
          <p>저자: {todayBook.author}</p>
          <p>번역가: {todayBook.translator}</p>
          <p>책 줄거리: {todayBook.kakaoDescription}</p>
          <p>카테고리: {todayBook.categoryName}</p>
          <p>가격: {todayBook.price}</p>
          <p>
            Purchase: You can purchase this book <a href="#">here</a>.
          </p>
        </div>
      </div>
      <div className="row mt-5">
        <div className="col-md-12">
          <h3 className="text-center">Related Books</h3>
          <div style={{ whiteSpace: "nowrap", overflowX: "auto" }}>
            {relatedBooks.map((book, index) => (
              <div
                style={{ display: "inline-block", margin: "10px" }}
                key={index}
              >
                <img
                  src={book.imageUrl}
                  className="img-fluid"
                  alt={book.title}
                  style={{ maxWidth: "200px", maxHeight: "300px" }}
                />
              </div>
            ))}
          </div>
        </div>
      </div>
    </div>
  );
}

export default MyPage;
