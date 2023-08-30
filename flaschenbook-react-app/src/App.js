import React, { useState, useEffect } from "react";
import AppContext from "./context";
import {
  BrowserRouter as Router,
  Routes,
  Route,
  Navigate,
} from "react-router-dom";
import Home from "./components/Home";
import Register from "./components/Register";
import Login from "./components/Login";
import Survey from "./components/Survey";
import MyPage from "./components/MyPage";

function App() {
  const [isLogged, setIsLogged] = useState(false);

  useEffect(() => {
    const checkSession = () => {
      const sessionInfo = localStorage.getItem("sessionInfo");
      setIsLogged(
        sessionInfo &&
          sessionInfo !== "undefined" &&
          sessionInfo !== "null" &&
          JSON.parse(sessionInfo)
          ? true
          : false
      );
    };

    window.addEventListener("storage", checkSession);

    return () => {
      window.removeEventListener("storage", checkSession);
    };
  }, []);

  useEffect(() => {
    const sessionInfo = localStorage.getItem("sessionInfo");
    setIsLogged(
      sessionInfo &&
        sessionInfo !== "undefined" &&
        sessionInfo !== "null" &&
        JSON.parse(sessionInfo)
        ? true
        : false
    );
  }, []);

  return (
    <AppContext.Provider value={{ setIsLogged, isLogged }}>
      <Router>
        <Routes>
          <Route path="/" element={<Home />} />
          <Route path="/register" element={<Register />} />
          <Route
            path="/login"
            element={isLogged ? <Navigate to="/" /> : <Login />}
          />
          <Route path="/survey" element={<Survey />} />
          <Route
            path="/my_page"
            element={isLogged ? <MyPage /> : <Navigate to="/login" />}
          />
        </Routes>
      </Router>
    </AppContext.Provider>
  );
}

export default App;
