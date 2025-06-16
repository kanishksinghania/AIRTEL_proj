import React, { useState, useEffect, useCallback } from "react";
import "./CheckCredit.css"; // Import the CSS file for styles

const CheckCredit = () => {
  const [creditSales, setCreditSales] = useState([]);
  const [customerName, setCustomerName] = useState("");
  const [message, setMessage] = useState("");
  const [paymentTypes, setPaymentTypes] = useState({});

  // 🔍 Fetch only credit sales (filtered by customer name if provided)
  const fetchCreditSales = useCallback(async () => {
    let url = `http://localhost:8080/AirtelAuth/check-credit`;

    if (customerName.trim() !== "") {
      url += `?customer_name=${encodeURIComponent(customerName.trim())}`;
    }

    try {
      const response = await fetch(url);
      const result = await response.json();

      console.log("API Response:", result);

      if (Array.isArray(result)) {
        setCreditSales(result.sort((a, b) => new Date(b.purchase_date) - new Date(a.purchase_date)));
        setMessage(result.length === 0 ? "No credit sales found." : "");
      } else {
        setMessage("No credit sales found.");
        setCreditSales([]);
      }
    } catch (error) {
      console.error("Error fetching credit sales:", error);
      setMessage("Failed to fetch credit sales.");
    }
  }, [customerName]);

  // 🔄 Fetch credit sales on load
  useEffect(() => {
    fetchCreditSales();
  }, [fetchCreditSales]);

  // ✅ Handle "Mark as Paid" Click
  const handleMarkAsPaid = async (id) => {
    if (!window.confirm("Are you sure you want to mark this transaction as Paid?")) return;

    const selectedPaymentType = paymentTypes[id] || "Cash"; // Default to Cash if not selected

    try {
      const response = await fetch(`http://localhost:8080/AirtelAuth/check`, {
        method: "POST",
        headers: { "Content-Type": "application/x-www-form-urlencoded" },
        body: new URLSearchParams({ id, payment_type: selectedPaymentType }),
      });

      const result = await response.json();
      alert(result.message || result.error);

      if (response.ok) {
        fetchCreditSales(); // Refresh credit sales after updating
      }
    } catch (error) {
      console.error("Error updating payment status:", error);
      alert("Failed to update payment status.");
    }
  };

  // ✅ Calculate Total Credit Amount
  const totalCreditAmount = creditSales.reduce((sum, record) => sum + (record.total_amount || 0), 0);

  return (
    <div className="check-credit-container">
      <h2>Check Credit Sales</h2>

      {/* Search Bar */}
      <div className="search-container">
        <input
          type="text"
          placeholder="Search by Customer Name"
          value={customerName}
          onChange={(e) => setCustomerName(e.target.value)}
        />
        <button onClick={fetchCreditSales} className="fetch-btn">🔍 Search</button>
      </div>

      {message && <p className="message">{message}</p>}

      {/* Credit Sales Table */}
      {creditSales.length > 0 && (
        <table className="credit-sales-table">
          <thead>
            <tr>
              <th>ID</th>
              <th>Customer Name</th>
              <th>Item Name</th>
              <th>Fabric Number</th>
              <th>Quantity (m)</th>
              <th>Total Amount (₹)</th>
              <th>Payment Status</th>
              <th>Purchase Date</th>
              <th>Payment Type</th>
              <th>Action</th>
            </tr>
          </thead>
          <tbody>
            {creditSales.map((record) => (
              <tr key={record.id}>
                <td>{record.id}</td>
                <td>{record.customer_name}</td>
                <td>{record.item_name}</td>
                <td>{record.fabric_number}</td>
                <td>{record.quantity_metres} m</td>
                <td>₹{record.total_amount}</td>
                <td>{record.payment_status}</td>
                <td>{record.purchase_date}</td>
                <td>
                  {/* ✅ Payment Type Dropdown */}
                  <select
                    value={paymentTypes[record.id] || "Cash"}
                    onChange={(e) =>
                      setPaymentTypes({ ...paymentTypes, [record.id]: e.target.value })
                    }
                  >
                    <option value="Cash">Cash</option>
                    <option value="UPI">UPI</option>
                    <option value="Bank">Bank</option>
                  </select>
                </td>
                <td>
                  {/* ✅ Mark as Paid Button */}
                  <button className="mark-paid-btn" onClick={() => handleMarkAsPaid(record.id)}>
                    ✅ Mark as Paid
                  </button>
                </td>
              </tr>
            ))}
          </tbody>
          {/* 🔹 Total Footer Row */}
          <tfoot>
            <tr>
              <td colSpan="5" style={{ fontWeight: "bold", textAlign: "right" }}>Total Credit Sales:</td>
              <td style={{ fontWeight: "bold" }}>₹{totalCreditAmount.toFixed(2)}</td>
              <td colSpan="4"></td>
            </tr>
          </tfoot>
        </table>
      )}
    </div>
  );
};

export default CheckCredit;
