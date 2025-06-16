import React from "react";
import { useNavigate } from "react-router-dom";
import './Sidebar.css'; // Import the CSS file for styles

const Sidebar = () => {
  const navigate = useNavigate();

  return (
    <div className="sidebar-container">
      <h2 className="sidebar-title">Dashboard</h2>

      {/* Functionality Buttons */}
      <button onClick={() => navigate("/")} className="sidebar-btn">Home</button>
      <button onClick={() => navigate("/inventory")} className="sidebar-btn">Inventory</button>
      <button onClick={() => navigate("/checkout")} className="sidebar-btn">Checkout</button>
      <button onClick={() => navigate("/check-sales")} className="sidebar-btn">Check Sales</button>
      <button onClick={() => navigate("/check-credit")} className="sidebar-btn">Check Credit</button>
      <button onClick={() => navigate("/add-customer")} className="sidebar-btn">➕ Add Customer</button>
      <button onClick={() => navigate("/view-customers")} className="sidebar-btn">📋 View Customers</button>
      <button onClick={() => navigate("/expense")} className="sidebar-btn">💰 Expense</button>
      <button onClick={() => navigate("/input-money")} className="sidebar-btn">💰 Input Money</button> 
    </div>
  );
};

export default Sidebar;