import React, { useState, useEffect } from "react";
import "./TodaysDescription.css"; // Import the CSS for styling

const TodaysDescription = () => {
  const [data, setData] = useState({
    total_sales: 0,
    upi_bank_sales: 0,
    total_expenses: 0,
    credit_sales: 0,
    party_entry: 0
  });
  const [message, setMessage] = useState("");

  useEffect(() => {
    fetchTodaysDescription();
  }, []);

  const fetchTodaysDescription = async () => {
    try {
      const response = await fetch("http://localhost:8080/AirtelAuth/description");
      const result = await response.json();

      if (result) {
        setData(result);
      } else {
        setMessage("No data available for today.");
      }
    } catch (error) {
      console.error("Error fetching today's summary:", error);
      setMessage("Failed to fetch data.");
    }
  };

  return (
    <div className="todays-description-container">
      <h2>📊 Today's Business Summary</h2>

      {message && <p className="message">{message}</p>}

      <div className="summary-box">
        <p><strong>💰 Total Sales:</strong> ₹{data.total_sales.toFixed(2)}</p>
        <p><strong>💳 UPI & Bank Sales:</strong> ₹{data.upi_bank_sales.toFixed(2)}</p>
        <p><strong>📉 Total Expenses:</strong> ₹{data.total_expenses.toFixed(2)}</p>
        <p><strong>🔴 Credit Sales:</strong> ₹{data.credit_sales.toFixed(2)}</p>
        <p><strong>💸 Party Entry:</strong> ₹{data.party_entry.toFixed(2)}</p>
      </div>
    </div>
  );
};

export default TodaysDescription;
