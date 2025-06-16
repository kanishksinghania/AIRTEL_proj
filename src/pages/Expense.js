import React, { useState, useEffect } from "react";
import "./Expense.css"; // Keeping the existing styling

const Expense = () => {
  const [description, setDescription] = useState("");
  const [expensePrice, setExpensePrice] = useState("");
  const [expenses, setExpenses] = useState([]);
  const [message, setMessage] = useState("");

  // ✅ Fetch expenses on load
  useEffect(() => {
    fetchExpenses();
  }, []);

  const fetchExpenses = async () => {
    try {
      const response = await fetch("http://localhost:8080/AirtelAuth/expenses");
      const result = await response.json();

      if (result.expenses.length === 0) {
        setMessage("ℹ️ No expenses found.");
        setExpenses([]);
      } else {
        setExpenses(result.expenses);
        setMessage("");
      }
    } catch (error) {
      console.error("Error fetching expenses:", error);
      setMessage("❌ Failed to fetch expenses.");
    }
  };

  // ✅ Handle expense submission
  const handleAddExpense = async (e) => {
    e.preventDefault();

    if (!description || !expensePrice) {
      setMessage("⚠️ Please fill in both fields.");
      return;
    }

    try {
      const response = await fetch("http://localhost:8080/AirtelAuth/expenses", {
        method: "POST",
        headers: { "Content-Type": "application/x-www-form-urlencoded" },
        body: new URLSearchParams({ description, expense_price: expensePrice }),
      });

      const result = await response.json();
      setMessage(result.message || result.error);

      if (response.ok) {
        // ✅ Reset form and refresh expense list
        setDescription("");
        setExpensePrice("");
        fetchExpenses();
      }
    } catch (error) {
      console.error("Error adding expense:", error);
      setMessage("❌ Failed to add expense.");
    }
  };

  // ✅ Calculate total expenses
  const totalExpenses = expenses.reduce((sum, exp) => sum + parseFloat(exp.expense_price), 0);

  return (
    <div className="expense-container">
      <h2>💰 Expenses</h2>

      {/* Add Expense Form */}
      <form onSubmit={handleAddExpense} className="expense-form">
        <input
          type="text"
          placeholder="Expense Description"
          value={description}
          onChange={(e) => setDescription(e.target.value)}
          required
        />
        <input
          type="number"
          step="0.01"
          placeholder="Expense Price (₹)"
          value={expensePrice}
          onChange={(e) => setExpensePrice(e.target.value)}
          required
        />
        <button type="submit" className="expense-btn">✔ Add Expense</button>
      </form>

      {message && <p className="message">{message}</p>}

      {/* Expense Table */}
      {expenses.length > 0 && (
        <table className="expense-table">
          <thead>
            <tr>
              <th>Description</th>
              <th>Amount (₹)</th>
              <th>Date & Time</th>
            </tr>
          </thead>
          <tbody>
            {expenses.map((expense) => (
              <tr key={expense.id}>
                <td>{expense.description}</td>
                <td>₹{expense.expense_price}</td>
                <td>{expense.updated_timestamp}</td>
              </tr>
            ))}
          </tbody>
          {/* Total Expenses Row */}
          <tfoot>
            <tr>
              <td><strong>Total Expenses</strong></td>
              <td><strong>₹{totalExpenses.toFixed(2)}</strong></td>
              <td></td>
            </tr>
          </tfoot>
        </table>
      )}
    </div>
  );
};

export default Expense;
