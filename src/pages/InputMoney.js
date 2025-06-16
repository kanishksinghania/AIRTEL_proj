import React, { useState, useEffect } from "react";
import "./InputMoney.css"; // Import CSS file

const InputMoney = () => {
  const [amount, setAmount] = useState("");
  const [purpose, setPurpose] = useState("");
  const [moneyInputs, setMoneyInputs] = useState([]);
  const [message, setMessage] = useState("");
  const [totalAmount, setTotalAmount] = useState(0);

  // ✅ Fetch all money inputs on load
  useEffect(() => {
    fetchMoneyInputs();
  }, []);

  const fetchMoneyInputs = async () => {
    try {
      const response = await fetch("http://localhost:8080/AirtelAuth/input-money");
      const result = await response.json();
      setMoneyInputs(result.money_inputs || []);
      setTotalAmount(result.total_amount || 0);
    } catch (error) {
      console.error("Error fetching money inputs:", error);
      setMessage("❌ Failed to fetch data.");
    }
  };

  // ✅ Handle money input submission
  const handleAddMoneyInput = async (e) => {
    e.preventDefault();
    if (!amount || !purpose) {
      setMessage("⚠️ Please enter both amount and purpose.");
      return;
    }

    try {
      const response = await fetch("http://localhost:8080/AirtelAuth/input-money", {
        method: "POST",
        headers: { "Content-Type": "application/x-www-form-urlencoded" },
        body: new URLSearchParams({ amount, purpose }),
      });

      const result = await response.json();
      setMessage(result.message || result.error);

      if (response.ok) {
        setAmount("");
        setPurpose("");
        fetchMoneyInputs();
      }
    } catch (error) {
      console.error("Error adding money input:", error);
      setMessage("❌ Failed to add money input.");
    }
  };

  return (
    <div className="input-money-container">
      <h2>💰 Input Money</h2>

      {message && <p className="message">{message}</p>}

      {/* Input Form */}
      <form onSubmit={handleAddMoneyInput} className="money-form">
        <input
          type="number"
          placeholder="Amount (₹)"
          value={amount}
          onChange={(e) => setAmount(e.target.value)}
          required
        />
        <input
          type="text"
          placeholder="Purpose"
          value={purpose}
          onChange={(e) => setPurpose(e.target.value)}
          required
        />
        <button type="submit" className="money-btn">✔ Add Entry</button>
      </form>

      {/* Money Entries Table */}
      {moneyInputs.length > 0 && (
        <table className="money-table">
          <thead>
            <tr>
              <th>Purpose</th>
              <th>Amount (₹)</th>
              <th>Date & Time</th>
            </tr>
          </thead>
          <tbody>
            {moneyInputs.map((entry) => (
              <tr key={entry.id}>
                <td>{entry.purpose}</td>
                <td>₹{entry.amount}</td>
                <td>{entry.timestamp}</td>
              </tr>
            ))}
          </tbody>
          <tfoot>
            <tr>
              <td><b>Total:</b></td>
              <td><b>₹{totalAmount}</b></td>
              <td></td>
            </tr>
          </tfoot>
        </table>
      )}
    </div>
  );
};

export default InputMoney;
