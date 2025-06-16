import React, { useState, useEffect } from "react";
import "./ViewCustomer.css"; // Import the relevant CSS file

const ViewCustomers = () => {
  const [customers, setCustomers] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");
  const [searchQuery, setSearchQuery] = useState(""); // Search input state

  // 🔄 Fetch customers when the page loads
  useEffect(() => {
    fetchCustomers();
  }, []);

  const fetchCustomers = async () => {
    try {
      const response = await fetch("http://localhost:8080/AirtelAuth/filecustomer");
      const data = await response.json();
      setCustomers(data);
      setLoading(false);
    } catch (error) {
      setError("Error fetching customers!");
      setLoading(false);
    }
  };

  // 🔍 Filter customers based on search query
  const filteredCustomers = customers.filter((customer) =>
    customer.customer_name.toLowerCase().includes(searchQuery.toLowerCase())
  );

  return (
    <div className="view-customers-container">
      <h2>📋 Customer List</h2>

      {/* Search Bar */}
      <div className="search-container">
        <input
          type="text"
          placeholder="🔍 Search by Customer Name"
          value={searchQuery}
          onChange={(e) => setSearchQuery(e.target.value)}
        />
      </div>

      {loading && <p>Loading...</p>}
      {error && <p style={{ color: "red" }}>{error}</p>}

      {/* Customer Table */}
      {filteredCustomers.length > 0 ? (
        <table className="customer-table">
          <thead>
            <tr>
              <th>ID</th>
              <th>Customer Name</th>
              <th>File Name</th>
              <th>Price (₹)</th>
              <th>Phone Number</th>
              <th>Address</th>
            </tr>
          </thead>
          <tbody>
            {filteredCustomers.map((customer) => (
              <tr key={customer.id}>
                <td>{customer.id}</td>
                <td>{customer.customer_name}</td>
                <td>{customer.file_name}</td>
                <td>₹{customer.price}</td>
                <td>{customer.phone_number}</td>
                <td>{customer.address}</td>
              </tr>
            ))}
          </tbody>
        </table>
      ) : (
        <p style={{ textAlign: "center", color: "yellow" }}>No customers found.</p>
      )}
    </div>
  );
};

export default ViewCustomers;
