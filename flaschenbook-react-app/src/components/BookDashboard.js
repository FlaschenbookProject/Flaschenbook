import "bootstrap/dist/css/bootstrap.min.css";
import "../css/Font.css"; // Font.css 파일을 import
import "../css/Survey.css";

function BookDashboard() {
    return (
      <div className="container">
        <div className="row">
          <div className="mx-auto text-center justify-content-center">
            <h3 className="survey-question-text">
            </h3>
            <img
              src="flaschenbook_dashboard.jpg" 
              alt="Flaschenbook Dashboard"
              className="img-fluid" 
            />
          </div>
        </div>
      </div>
    );
  }
  
  export default BookDashboard;