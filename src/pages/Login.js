import React, { useState } from "react";
import { useNavigate } from "react-router-dom";
import { loginUser } from "../api/userApi";
import "./Login.css";

const Login = () => {
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [error, setError] = useState(null);
  const navigate = useNavigate();

  const handleLogin = async (e) => {
    e.preventDefault();

    try {
      console.log("Attempting login with:", email, password); 
      const response = await loginUser({ email, password });
      console.log("Server response:", response);

      if (response.message === "Login successful!") {
        
        localStorage.setItem("user", JSON.stringify(response));
        navigate("/login-success", { state: { user: response } });

      } else {
        setError("Invalid email or password.");
      }
    } catch (err) {
      setError("Login failed. Please try again.");
    }
  };

  return (
    <div className="login-container">
      <h2>Login</h2>
      {error && <p className="error-message">{error}</p>}
      <form onSubmit={handleLogin}>
        <input
          type="email"
          placeholder="Email"
          value={email}
          onChange={(e) => setEmail(e.target.value)}
          required
        />
        <input
          type="password"
          placeholder="Password"
          value={password}
          onChange={(e) => setPassword(e.target.value)}
          required
        />
        <button type="submit">Login</button>
      </form>
    </div>
  );
};

export default Login;
