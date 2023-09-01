import "../css/Logo.css"; // 전역 CSS 파일을 import
import "../css/Font.css"; // Font.css 파일을 import
import AppContext from "../context";
import React, { useContext } from "react";
import { Link } from "react-router-dom";
import { useNavigate } from "react-router-dom";

// MainLogo.js
function MainLogo() {
  const { setIsLogged, isLogged } = useContext(AppContext);
  const navigate = useNavigate();

  const handleLogout = () => {
    const sessionInfo = JSON.parse(localStorage.getItem("sessionInfo"));
    fetch("/api/users/logout", {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify(sessionInfo),
    })
      .then((response) => response.text())
      .then((data) => {
        console.log("Success:", data);
        localStorage.removeItem("sessionInfo");
        setIsLogged(false);
        navigate("/");
      })
      .catch((error) => {
        console.error("Error:", error);
      });
  };

  return (
    <div className="logo-container">
      {/* SVG 배경 이미지 */}
      <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 1440 320">
        <path
          fill="#0099ff"
          fill-opacity="0.3"
          d="M0,160L14.1,170.7C28.2,181,56,203,85,224C112.9,245,141,267,169,250.7C197.6,235,226,181,254,144C282.4,107,311,85,339,85.3C367.1,85,395,107,424,101.3C451.8,96,480,64,508,85.3C536.5,107,565,181,593,197.3C621.2,213,649,171,678,160C705.9,149,734,171,762,160C790.6,149,819,107,847,128C875.3,149,904,235,932,229.3C960,224,988,128,1016,90.7C1044.7,53,1073,75,1101,85.3C1129.4,96,1158,96,1186,80C1214.1,64,1242,32,1271,64C1298.8,96,1327,192,1355,224C1383.5,256,1412,224,1426,208L1440,192L1440,0L1425.9,0C1411.8,0,1384,0,1355,0C1327.1,0,1299,0,1271,0C1242.4,0,1214,0,1186,0C1157.6,0,1129,0,1101,0C1072.9,0,1045,0,1016,0C988.2,0,960,0,932,0C903.5,0,875,0,847,0C818.8,0,791,0,762,0C734.1,0,706,0,678,0C649.4,0,621,0,593,0C564.7,0,536,0,508,0C480,0,452,0,424,0C395.3,0,367,0,339,0C310.6,0,282,0,254,0C225.9,0,198,0,169,0C141.2,0,113,0,85,0C56.5,0,28,0,14,0L0,0Z"
        ></path>
        {/* SVG의 경로 및 텍스트 내용 */}
      </svg>
      {/* 로고 이미지 */}
      {/* 로고 이미지 */}
      <Link to="/">
        <img src="/logo.png" alt="로고" className="logo-image" />
      </Link>
      <div
        className={`login-controls ${isLogged ? "logged-in" : "logged-out"}`}
      >
        {isLogged ? (
          <>
            <a href="#" onClick={handleLogout} className="link-style">
              로그아웃
            </a>
            <Link to="/my-page" className="link-style">
              날 위한 서재
            </Link>
          </>
        ) : (
          <>
            <Link to="/register" className="link-style">
              회원가입
            </Link>
            <Link to="/login" className="link-style">
              로그인
            </Link>
          </>
        )}
      </div>
    </div>
  );
}

export default MainLogo;
