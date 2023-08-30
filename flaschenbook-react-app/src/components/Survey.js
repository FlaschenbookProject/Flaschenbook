import React, { useState, useEffect } from "react";
import { useNavigate } from "react-router-dom";

function Survey() {
  const [contentPairs, setContentPairs] = useState([""]);
  const navigate = useNavigate();
  const [selectedContents, setSelectedContents] = useState([]);

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
          navigate("/");
        })
        .catch((error) => console.error("Error:", error));
    }
  };

  return (
    <div>
      {contentPairs.length ? (
        <div>
          <h2>Select a content:</h2>
          <button onClick={() => handleSelection(0)}>
            {contentPairs[0].content1 ? contentPairs[0].content1.content : ""}
          </button>
          <button onClick={() => handleSelection(1)}>
            {contentPairs[0].content2 ? contentPairs[0].content2.content : ""}
          </button>
        </div>
      ) : (
        <div>
          <h2>Selection completed.</h2>
          <button onClick={() => navigate("/")}>let's get started~!</button>
        </div>
      )}
    </div>
  );
}

export default Survey;
