import React from "react";
import { useNavigate } from "react-router-dom";
import './Home.css'; 

const Home = () => {
  const navigate = useNavigate();

  return (
    <div className="home-container">
      <h1 className="welcome-heading">Welcome to Royal Fabrics!</h1>

      <div className="button-container">
        <button className="nav-button" onClick={() => navigate("/register")}>Create New Account</button>
        <button className="nav-button" onClick={() => navigate("/login")}>Login to Existing Account</button>
        <button className="nav-button" onClick={() => navigate("/admin-details")}>Get Admin Role Details</button>
        <button className="nav-button" onClick={() => navigate("/user-details")}>Get User Role Details</button>
        <button className="nav-button" onClick={() => navigate("/all-users")}>Get All User Details</button>
      </div>
    </div>
  );
};

export default Home;