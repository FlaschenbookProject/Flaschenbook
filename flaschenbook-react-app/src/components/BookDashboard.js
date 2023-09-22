import "bootstrap/dist/css/bootstrap.min.css";
import "../css/Font.css"; // Font.css 파일을 import
import "../css/Survey.css";

function BookDashboard() {
  return (
    <div className="container">
      <div className="row">
        <div className="col-md-12 mx-auto text-center justify-content-center">
          <h3 className="survey-question-text">
            연월별 도서 카테고리 출판 추이
          </h3>
          <img
            src="1.png" 
            alt="Flaschenbook Dashboard"
            className="img-fluid" 
          />
        </div>
      </div>
      <div className="row" style={{ marginTop: '50px', display: 'flex' }}>
        <div className="col-md-6 mx-auto text-center justify-content-center" style={{ flex: 1 }}>
          <h3 className="survey-question-text">
            베스트셀러 카테고리
          </h3>
          <img
            src="2.png" 
            alt="Flaschenbook Dashboard"
            className="img-fluid" 
            style={{ width: '100%', height: '90%' }}
          />
        </div>
        <div className="col-md-6 mx-auto text-center justify-content-center" style={{ flex: 1 }}>
          <h3 className="survey-question-text">
            가격대별 베스트셀러
          </h3>
          <img
            src="3.png" 
            alt="Flaschenbook Dashboard"
            className="img-fluid" 
            style={{ width: '100%' }}
          />
        </div>
      </div>
      <div className="row">
        <div className="col-md-12 mx-auto text-center justify-content-center" >
          <h3 className="survey-question-text">
            베스트셀러 리뷰 반응
          </h3>
          <img
            src="4.png" 
            alt="Flaschenbook Dashboard"
            className="img-fluid" 
          />
        </div>
      </div>
    </div>
  );
}

export default BookDashboard;
