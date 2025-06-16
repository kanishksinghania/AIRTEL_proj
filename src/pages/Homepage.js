import React, { useState } from "react";
import { useNavigate } from "react-router-dom";
import Sidebar from "../pages/Sidebar";
import ContentArea from "../pages/ContentArea";
import "./Homepage.css";

const HomePage = () => {
  const navigate = useNavigate();
  const [selectedFeature, setSelectedFeature] = useState("welcome");

  return (
    <div className="homepage-container">
      {/* Sidebar (Left Panel) */}
      <Sidebar setSelectedFeature={setSelectedFeature} />

      {/* Main Content (Right Panel) */}
      <div className="main-content">
        {/* User Management Button */}
        <button 
          className="user-management-button"
          onClick={() => navigate("/user-management")}
          aria-label="User Management"
        >
          User Management
        </button>

        {/* New Button for Today's Description */}
        <div className="sales-report-section">
          <h3>📊 Daily Business Overview</h3>
          <button
            className="sales-btn"
            onClick={() => setSelectedFeature("todaysDescription")}
          >
            📅 Show Today's Description
          </button>
        </div>

        {/* Dynamic Content Display */}
        <ContentArea selectedFeature={selectedFeature} />
      </div>
    </div>
  );
};

export default HomePage;
