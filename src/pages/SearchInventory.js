import React, { useState } from "react";
import "./SearchInventory.css"; // Add a CSS file for table styling

const SearchInventory = () => {
  const [searchQuery, setSearchQuery] = useState("");
  const [results, setResults] = useState([]);

  const handleSearch = async () => {
    try {
      const response = await fetch(`http://localhost:8080/AirtelAuth/inventory?item_name=${searchQuery}`);
      const data = await response.json();
      setResults(data);
    } catch (error) {
      console.error("Error fetching inventory:", error);
    }
  };

  return (
    <div className="search-inventory-container">
      <h3>🔍 Search Inventory</h3>
      
      <div className="search-bar">
        <input 
          type="text" 
          placeholder="Enter Item Name" 
          value={searchQuery} 
          onChange={(e) => setSearchQuery(e.target.value)} 
        />
        <button onClick={handleSearch}>Search</button>
      </div>

      {/* Inventory Table */}
      {results.length > 0 ? (
        <table className="inventory-table">
          <thead>
            <tr>
              <th>Item Name</th>
              <th>Fabric Number</th>
              <th>Quantity (m)</th>
              <th>Created At</th>
              <th>Last Updated</th>
            </tr>
          </thead>
          <tbody>
            {results.map((item, index) => (
              <tr key={index}>
                <td>{item.item_name}</td>
                <td>{item.fabric_number}</td>
                <td>{item.quantity_metres} m</td>
                <td>{new Date(item.created_at).toLocaleString()}</td> {/* ✅ Format Date */}
                <td>{new Date(item.updated_at).toLocaleString()}</td> {/* ✅ Format Date */}
              </tr>
            ))}
          </tbody>
        </table>
      ) : (
        <p>No results found.</p>
      )}
    </div>
  );
};

export default SearchInventory;
