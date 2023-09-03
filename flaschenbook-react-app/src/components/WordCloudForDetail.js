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
  colors: [ '#FF0000', 'FA8072', '#FF4500', '#FFFF00', '00FF00', '#7CFC00', '#ADFF2F', '#00FF00', '#006400', '#2E8B57', '#90EE90', '#00FF7F', '#20B2AA', '#00FFFF', '#0000FF', '#483D8B', '#7B68EE', '#9400D3', '#E6E6FA', '#EE82EE', '#4B0082', '#FF1493', '#FF00FF', '#FFB6C1', '#FFFFE0', '#FFFFF0', '#A9A9A9', '#696969', '#000000', '#FFFFFF'],
};


export const WordCloudForDetail = ({ isbn }) => {
  const [SourceText, setSourceText] = useState("");


  useEffect(() => {
    const fetchData = async () => {
      if (!isbn) return;
      try {
        const response = await fetch("/api/books/word_cloud_detail?isbn=" + isbn);
        const data = await response.json();
        setSourceText(data[0]);
      } catch (error) {
        console.error(error);
      }
    };
    fetchData();
  }, [isbn]);

  const Words = getWordsArray(SourceText);

  return (
    <div
      className="justify-content-center"
      style={{ display: "flex", width: "100%", height: "400px",  backgroundColor: "#5AB2FF", padding: "70px"}}
    >
        {SourceText === null || SourceText === "" ? (
          <h4>리뷰가 없습니다.</h4>
        ) : (
          <WordCloud words={Words} options={options} maxWords={50}/>
        )}
    </div>
  );
};
