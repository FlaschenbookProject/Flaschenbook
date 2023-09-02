import React, { useState, useEffect } from "react";
import WordCloud from "react-wordcloud";

function getWordsArray(text) {
  const words = text.split(" ");
  const wordsCount = {};

  words.forEach((word) => {
    wordsCount[word] = (wordsCount[word] || 0) + 1;
  });

  return Object.keys(wordsCount)
    .filter((word) => wordsCount[word] > 5)
    .map((word) => ({
      text: word,
      value: wordsCount[word],
    }));
}

const options = {
  rotations: 2,
  rotationAngles: [0, 90],
  fontSizes: [20, 60], // 이 부분을 조정하여 글자 크기를 변경
  fontFamily: "Bum",
};

export const WordCloudComponent = ({ isbn }) => {
  const [positiveSourceText, setPositiveSourceText] = useState("");
  const [negativeSourceText, setNegativeSourceText] = useState("");

  useEffect(() => {
    const fetchData = async () => {
      if (!isbn) return;
      try {
        const response = await fetch("/api/my-page/book-words?isbn=" + isbn);
        const data = await response.json();
        setPositiveSourceText(data[0]);
        setNegativeSourceText(data[1]);
      } catch (error) {
        console.error(error);
      }
    };
    fetchData();
  }, [isbn]);

  const positiveWords = getWordsArray(positiveSourceText);
  const negativeWords = getWordsArray(negativeSourceText);

  return (
    <div
      className="justify-content-center"
      style={{ display: "flex", width: "100%", height: "400px", justifyContent: "center"}}
    >
      <div
        className="text-center justify-content-center"
        style={{
          flex: 1,
          border: "none",
        }}
      >
        <h3 className="survey-question-text text-center">
          독자들은 이런 점을 좋아해요
        </h3>
        {positiveSourceText === null || positiveSourceText === "" ? (
          <h4>리뷰가 없습니다.</h4>
        ) : (
          <WordCloud words={positiveWords} options={options} />
        )}
      </div>
      <div
        className="text-center justify-content-center"
        style={{
          flex: 1,
          border: "none",
        }}
      >
        <h3 className="survey-question-text text-center">
          독자들은 이런 점을 아쉬워해요
        </h3>
        {negativeSourceText === null || negativeSourceText === "" ? (
          <h4>리뷰가 없습니다.</h4>
        ) : (
          <WordCloud words={negativeWords} options={options} />
        )}
      </div>
    </div>
  );
};
