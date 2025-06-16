import React, { useState } from "react";
import './CheckSales.css';

const CheckSales = () => {
  const [sales, setSales] = useState([]);
  const [day, setDay] = useState("");
  const [month, setMonth] = useState("");
  const [year, setYear] = useState("");
  const [customerName, setCustomerName] = useState("");
  const [message, setMessage] = useState("");
  const [loading, setLoading] = useState(false);

  // ✅ Fetch Sales Based on Filters
  const fetchSales = async () => {
    setLoading(true);
    let url = `http://localhost:8080/AirtelAuth/check`;

    const params = new URLSearchParams();
    if (customerName.trim() !== "") params.append("customer_name", customerName.trim());
    if (day) params.append("day", day);
    if (month) params.append("month", month);
    if (year) params.append("year", year);

    if (params.toString()) url += `?${params.toString()}`;

    try {
      console.log("Fetching from:", url);
      const response = await fetch(url);
      if (!response.ok) throw new Error(`Server responded with status: ${response.status}`);

      const result = await response.json();
      console.log("API Response:", result);

      if (!result.sales || !Array.isArray(result.sales)) {
        setMessage("Invalid response from server.");
        setSales([]);
        return;
      }

      setSales(result.sales.sort((a, b) => new Date(b.purchase_date) - new Date(a.purchase_date)));
      setMessage(result.sales.length === 0 ? "No sales found for the selected criteria." : "");

    } catch (error) {
      console.error("Error fetching sales:", error);
      setMessage("Failed to fetch sales.");
    } finally {
      setLoading(false);
    }
  };

  // ✅ Calculate Total Sales Amount
  const totalSalesAmount = sales.reduce((sum, record) => sum + (record.total_amount || 0), 0);

  return (
    <div className="check-sales-container">
      <h2>Check Sales</h2>

      {/* Date Filters */}
      <div className="filter-container">
        <select value={day} onChange={(e) => setDay(e.target.value)}>
          <option value="">Day</option>
          {[...Array(31).keys()].map((d) => (
            <option key={d + 1} value={d + 1}>{d + 1}</option>
          ))}
        </select>

        <select value={month} onChange={(e) => setMonth(e.target.value)}>
          <option value="">Month</option>
          {[...Array(12).keys()].map((m) => (
            <option key={m + 1} value={m + 1}>{m + 1}</option>
          ))}
        </select>

        <select value={year} onChange={(e) => setYear(e.target.value)}>
          <option value="">Year</option>
          {[2024, 2025, 2026].map((y) => (
            <option key={y} value={y}>{y}</option>
          ))}
        </select>

        <input
          type="text"
          placeholder="Customer Name (optional)"
          value={customerName}
          onChange={(e) => setCustomerName(e.target.value)}
        />

        <button onClick={fetchSales} className="fetch-btn" disabled={loading}>
          {loading ? "Loading..." : "🔍 Search"}
        </button>
      </div>

      {/* Message */}
      {message && <p className="message">{message}</p>}

      {/* Sales Table */}
      {sales.length > 0 && (
        <table className="sales-table">
          <thead>
            <tr>
              <th>ID</th>
              <th>Customer Name</th>
              <th>Item Name</th>
              <th>Fabric Number</th>
              <th>Quantity (m)</th>
              <th>Price Per Metre (₹)</th>
              <th>Total Amount (₹)</th>
              <th>Payment Status</th>
              <th>Purchase Date</th>
            </tr>
          </thead>
          <tbody>
            {sales.map((record, index) => (
              <tr key={index}>
                <td>{record.id}</td>
                <td>{record.customer_name || "N/A"}</td>
                <td>{record.item_name || "N/A"}</td>
                <td>{record.fabric_number || "N/A"}</td>
                <td>{record.quantity_metres || "0"} m</td>
                <td>₹{record.price_per_metre || "0.00"}</td>
                <td>₹{record.total_amount || "0.00"}</td>
                <td>{record.payment_status || "N/A"}</td>
                <td>{record.purchase_date || "N/A"}</td>
              </tr>
            ))}
          </tbody>
          {/* 🔹 Total Footer Row */}
          <tfoot>
            <tr>
              <td colSpan="6" style={{ fontWeight: "bold", textAlign: "right" }}>Total Sales:</td>
              <td style={{ fontWeight: "bold" }}>₹{totalSalesAmount.toFixed(2)}</td>
              <td colSpan="2"></td>
            </tr>
          </tfoot>
        </table>
      )}
    </div>
  );
};

export default CheckSales;
