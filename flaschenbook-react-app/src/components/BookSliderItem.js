import '../css/Main.css';
import React, { useState } from 'react';

function BookSliderItem(props) {
  const { book } = props;
  const [isHovered, setIsHovered] = useState(false);
  const handleMouseEnter = () => {
    setIsHovered(true);
  };

  const handleMouseLeave = () => {
    setIsHovered(false);
  };


  return (
    <div
      className="book-slider-item"
      onMouseEnter={handleMouseEnter}
      onMouseLeave={handleMouseLeave}
    >
      <img src={book.imageUrl} alt={book.title} className="book-image" />
      {isHovered && (
        <div className="book-info">
          <h4 className="book-hover-title">{book.title}</h4>
          <p className="book-description">{book.author}</p>
          <p className="book-description">{book.description}</p>
        </div>
      )}
    </div>
  );
}

export default BookSliderItem;


