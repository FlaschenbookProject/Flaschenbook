import React from "react";
import "bootstrap/dist/css/bootstrap.min.css";

function MyPage(props) {
  return (
    <div className="container mt-5">
      <div className="row">
        <div className="col-md-6 mx-auto text-center">
          <img
            src="book-cover.jpg"
            alt="Book Cover"
            className="img-thumbnail"
          />
          <h2>Book Title</h2>
          <p>Author: Author Name</p>
          <p>Summary: This is the summary of the book.</p>
          <p>
            Purchase: You can purchase this book <a href="#">here</a>.
          </p>
        </div>
      </div>
      <div className="row mt-5">
        <div className="col-md-12">
          <h3 className="text-center">Related Books</h3>
          <div
            id="carouselExampleControls"
            className="carousel slide"
            data-bs-ride="carousel"
          >
            <div className="carousel-inner">
              <div className="carousel-item active">
                <img
                  src="related-book1.jpg"
                  className="d-block mx-auto"
                  alt="Related Book 1"
                />
              </div>
              <div className="carousel-item">
                <img
                  src="related-book2.jpg"
                  className="d-block mx-auto"
                  alt="Related Book 2"
                />
              </div>
              <div className="carousel-item">
                <img
                  src="related-book3.jpg"
                  className="d-block mx-auto"
                  alt="Related Book 3"
                />
              </div>
              <div className="carousel-item">
                <img
                  src="related-book4.jpg"
                  className="d-block mx-auto"
                  alt="Related Book 4"
                />
              </div>
              <div className="carousel-item">
                <img
                  src="related-book5.jpg"
                  className="d-block mx-auto"
                  alt="Related Book 5"
                />
              </div>
            </div>
            <button
              className="carousel-control-prev"
              type="button"
              data-bs-target="#carouselExampleControls"
              data-bs-slide="prev"
            >
              <span
                className="carousel-control-prev-icon"
                aria-hidden="true"
              ></span>
              <span className="visually-hidden">Previous</span>
            </button>
            <button
              className="carousel-control-next"
              type="button"
              data-bs-target="#carouselExampleControls"
              data-bs-slide="next"
            >
              <span
                className="carousel-control-next-icon"
                aria-hidden="true"
              ></span>
              <span className="visually-hidden">Next</span>
            </button>
          </div>
        </div>
      </div>
    </div>
  );
}

export default MyPage;
