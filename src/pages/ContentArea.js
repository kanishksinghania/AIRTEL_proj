import React from "react";
import TodaysDescription from "../pages/TodaysDescription"; // Import the new component
import "./ContentArea.css"; 

const ContentArea = ({ selectedFeature }) => {
  return (
    <div className="content-area">
      {selectedFeature === "welcome" && <h1>Welcome to Royal Fabrics</h1>}
      {selectedFeature === "todaysDescription" && <TodaysDescription />}
    </div>
  );
};

export default ContentArea;
