import React, { useState, useContext } from "react";
import { useNavigate } from "react-router-dom";
import AppContext from "../context";

function Register(props) {
  const [username, setUsername] = useState("");
  const [password, setPassword] = useState("");
  const [gender, setGender] = useState("");
  const [email, setEmail] = useState("");
  const [emailError, setEmailError] = useState("");
  const [showPassword, setShowPassword] = useState(false);
  const [birthdate, setBirthdate] = useState("");
  const { setIsLogged } = useContext(AppContext);

  const navigate = useNavigate();

  const handleEmailChange = (event) => {
    const email = event.target.value;
    setEmail(email);

    const emailPattern = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    if (!emailPattern.test(email)) {
      setEmailError("유효하지 않은 이메일입니다.");
    } else {
      setEmailError("");
    }
  };

  const handleSubmit = (event) => {
    event.preventDefault();

    const user = { username, email, password, gender, birthdate };

    fetch("/api/users/register", {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify(user),
    })
      .then((response) => {
        if (!response.ok) {
          throw response;
        }
        return response.json();
      })
      .then((data) => {
        console.log("Success:", data);

        // 회원가입 성공하면 바로 로그인
        return fetch("/api/users/login", {
          method: "POST",
          headers: { "Content-Type": "application/json" },
          body: JSON.stringify({ email, password }),
        });
      })
      .then((response) => response.json())
      .then((data) => {
        console.log("Success:", data);
        const sessionInfo = {
          userId: data.userId,
          sessionId: data.sessionId,
        };
        console.log("Login Success:", data);
        localStorage.setItem("sessionInfo", JSON.stringify(sessionInfo));
        localStorage.setItem("username", data.username);

        setIsLogged(true); // 로그인 성공하면 isLogged 상태를 업데이트합니다.
        navigate("/survey");
      })
      .catch((error) => {
        if (error instanceof Response) {
          error.json().then((errorMessage) => {
            setEmailError(errorMessage.message);
            console.error(errorMessage);
          });
        } else {
          console.error("Error:", error);
        }
      });
  };

  const handleGoogleLogin = () => {
    fetch("/api/users/oauth_login", {
      method: "POST",
      headers: { "Content-Type": "application/json" },
    })
      .then((response) => response.json())
      .then((data) => {
        console.log("Success:", data);
      })
      .catch((error) => {
        console.error("Error:", error);
      });
  };

  return (
    <div className="container d-flex justify-content-center">
      <form onSubmit={handleSubmit}>
        <h3>Sign In</h3>
        <div className="mb-3">
          <label htmlFor="username" className="form-label">
            Username
          </label>
          <input
            type="text"
            className="form-control"
            id="username"
            value={username}
            onChange={(e) => setUsername(e.target.value)}
            autocomplete="username"
          />
        </div>
        <div className="mb-3">
          <label htmlFor="email" className="form-label">
            Email
          </label>
          <input
            type="email"
            className="form-control"
            id="email"
            value={email}
            onChange={handleEmailChange}
            autoComplete="email"
          />
          {emailError && (
            <div className="alert alert-warning mt-2">{emailError}</div>
          )}
        </div>
        <div className="mb-3">
          <label htmlFor="password" className="form-label">
            Password
          </label>
          <div className="input-group mb-3">
            <input
              type={showPassword ? "text" : "password"}
              className="form-control"
              id="password"
              value={password}
              onChange={(e) => setPassword(e.target.value)}
            />
            <div className="input-group-append">
              <button
                className="btn btn-outline-secondary border-left-0"
                type="button"
                onClick={() => setShowPassword(!showPassword)}
              >
                {showPassword ? (
                  <i className="fa fa-eye-slash"></i>
                ) : (
                  <i className="fa fa-eye"></i>
                )}
              </button>
            </div>
          </div>
        </div>
        <div className="mb-3">
          <label htmlFor="birthdate" className="form-label">
            Birthdate
          </label>
          <input
            type="date"
            className="form-control"
            id="birthdate"
            value={birthdate}
            onChange={(e) => setBirthdate(e.target.value)}
          />
        </div>
        <div className="mb-3">
          <div>
            <label htmlFor="gender" className="form-label">
              Gender
            </label>
          </div>
          <div className="form-check form-check-inline">
            <input
              className="form-check-input"
              type="radio"
              name="gender"
              id="male"
              value="M"
              checked={gender === "M"}
              onChange={(e) => setGender(e.target.value)}
            />
            <label className="form-check-label" htmlFor="male">
              Male
            </label>
          </div>
          <div className="form-check form-check-inline">
            <input
              className="form-check-input"
              type="radio"
              name="gender"
              id="female"
              value="F"
              checked={gender === "F"}
              onChange={(e) => setGender(e.target.value)}
            />
            <label className="form-check-label" htmlFor="female">
              Female
            </label>
          </div>
        </div>
        <div className="d-grid">
          <button type="submit" className="btn btn-primary">
            Submit
          </button>
        </div>
      </form>
    </div>
  );
}

export default Register;
