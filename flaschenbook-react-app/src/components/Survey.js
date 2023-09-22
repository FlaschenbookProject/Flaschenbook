import React, { useState, useEffect } from "react";
import { useNavigate } from "react-router-dom";
import "bootstrap/dist/css/bootstrap.min.css";
import "../css/Font.css"; // Font.css 파일을 import
import "../css/Survey.css";

function Survey() {
  const [contentPairs, setContentPairs] = useState([""]);
  const navigate = useNavigate();
  const [selectedContents, setSelectedContents] = useState([]);
  const username = localStorage.getItem("username");

  useEffect(() => {
    fetch("/api/survey/content-pairs")
      .then((response) => response.json())
      .then((data) => setContentPairs(data))
      .catch((error) => console.error(error));
  }, []);

  const handleSelection = (index) => {
    const selectedContent =
      index === 0 ? contentPairs[0].content1 : contentPairs[0].content2;
    setSelectedContents([...selectedContents, selectedContent]);
    setContentPairs(contentPairs.slice(1));

    // 마지막 선택지일 경우 선택했을 때 서버로 선택정보 보냄
    if (contentPairs.length === 1) {
      const sessionInfo = JSON.parse(localStorage.getItem("sessionInfo"));
      const userId = sessionInfo.userId;

      fetch("/api/survey/selected-contents", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({
          userId,
          selectedContents: [...selectedContents, selectedContent],
        }),
      })
        .then((response) => {
          if (!response.ok) {
            throw new Error("Network response was not ok");
          }
          console.log("Success");
        })
        .catch((error) => console.error("Error:", error));
    }
  };

  return (
    <div className="container d-flex justify-content-center align-items-center">
      {contentPairs.length ? (
        <div>
          <div className="text-center mb-4">
            <h3 className="survey-question-text">
              "{username}" 님에게 바다에서 쪽지가 도착했습니다.
            </h3>
            <h2 className="survey-question-text">
              더 끌리는 쪽지를 골라 주세요.
            </h2>
          </div>
          <div className="row justify-content-center">
            <div className="col-auto">
              <div
                className="card"
                style={{
                  width: "18rem",
                  height: "20rem",
                  border: "none",
                  borderRadius: "10px",
                }}
              >
                <div
                  className="card-body"
                  style={{ height: "18rem", padding: 0 }}
                >
                  <div
                    className="h-100 overflow-auto p-3"
                    style={{
                      backgroundColor: "#0099ff",
                      color: "white",
                      borderRadius: "10px",
                    }}
                    onClick={() => handleSelection(0)}
                  >
                    <p className="survey-content-text">
                      {contentPairs[0].content1
                        ? contentPairs[0].content1.content
                        : ""}
                    </p>
                  </div>
                </div>
              </div>
            </div>
            <div
              className="col-auto d-flex align-items-center justify-content-center mx-4"
              style={{ fontSize: "3rem", height: "20rem" }}
            >
              🤔
            </div>
            <div className="col-auto">
              <div
                className="card"
                style={{
                  width: "18rem",
                  height: "20rem",
                  border: "none",
                  borderRadius: "10px",
                }}
              >
                <div
                  className="card-body"
                  style={{ height: "18rem", padding: 0 }}
                >
                  <div
                    className="h-100 overflow-auto p-3"
                    style={{
                      backgroundColor: "#0099ff",
                      color: "white",
                      borderRadius: "10px",
                    }}
                    onClick={() => handleSelection(1)}
                  >
                    <p className="survey-content-text">
                      {contentPairs[0].content2
                        ? contentPairs[0].content2.content
                        : ""}
                    </p>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
      ) : (
        <div
          className="d-flex justify-content-center align-items-center"
          style={{ minHeight: "50vh" }}
        >
          <div
            className="d-flex justify-content-center align-items-center"
            style={{ minHeight: "50vh" }}
          >
            <div>
              <h2 className="text-center mb-4 survey-question-text">🎉</h2>
              <button className="btn btn-primary" onClick={() => setTimeout(() => navigate("/"), 3000)}>
                시작하기
              </button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
}

export default Survey;
