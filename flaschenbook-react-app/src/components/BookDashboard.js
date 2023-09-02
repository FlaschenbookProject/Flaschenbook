import React, { useState, useEffect } from "react";
import "bootstrap/dist/css/bootstrap.min.css";
import "../css/Font.css"; // Font.css 파일을 import
import "../css/Survey.css";

function BookDashboard() {
  return (
    <div className="container">
      <div className="row">
        <div className="col-md-6 mx-auto text-center">
          <h3 className="survey-question-text">
            이런 도서를 선호해요
          </h3>
        </div>
      </div>
    </div>
  );
}

export default BookDashboard;
