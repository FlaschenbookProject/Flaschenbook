import React, { useState } from "react";
import { useNavigate } from "react-router-dom";

function Survey() {
  const [sentencePairs, setSentencePairs] = useState([
    ["The sun is shining.", "It's a beautiful day."],
    ["I am learning React.", "Programming is fun."],
    ["pick me.", "i'm hungry"],
    ["great", "bored"],
    ["literature", "SF"],
  ]);
  const navigate = useNavigate();
  const [selectedSentences, setSelectedSentences] = useState([]);

  const handleSelection = (index) => {
    setSelectedSentences([...selectedSentences, sentencePairs[0][index]]);
    setSentencePairs(sentencePairs.slice(1));
  };

  return (
    <div>
      {sentencePairs.length ? (
        <div>
          <h2>Select a sentence:</h2>
          <button onClick={() => handleSelection(0)}>
            {sentencePairs[0][0]}
          </button>
          <button onClick={() => handleSelection(1)}>
            {sentencePairs[0][1]}
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
