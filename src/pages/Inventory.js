import React, { useState } from "react";
import SearchInventory from "../pages/SearchInventory"; // ✅ Check path
import AddInventory from "../pages/AddInventory"; // ✅ Check path
import UpdateInventory from "../pages/UpdateInventory"; // ✅ Check path
import DeleteInventory from "../pages/DeleteInventory"; // ✅ Check path
import './Inventory.css'; // Import the CSS file for styles

const Inventory = () => {
  const [selectedOption, setSelectedOption] = useState(null);

  return (
    <div className="inventory-container">
      <h2>Inventory Management</h2>

      {/* Show Main Inventory Options */}
      {!selectedOption && (
        <div className="button-container">
          <button onClick={() => setSelectedOption("search")} className="inv-btn">🔍 Search Inventory</button>
          <button onClick={() => setSelectedOption("add")} className="inv-btn">➕ Add New Item</button>
          <button onClick={() => setSelectedOption("update")} className="inv-btn">✏️ Update Existing Item</button>
          <button onClick={() => setSelectedOption("delete")} className="inv-btn">❌ Delete an Item</button>
        </div>
      )}

      {/* Conditional Rendering Based on Selected Option */}
      {selectedOption === "search" && <SearchInventory />}
      {selectedOption === "add" && <AddInventory />}
      {selectedOption === "update" && <UpdateInventory />}
      {selectedOption === "delete" && <DeleteInventory />}

      {/* Back Button to Main Inventory Options */}
      {selectedOption && (
        <button onClick={() => setSelectedOption(null)} className="back-btn">🔙 Back</button>
      )}
    </div>
  );
};

export default Inventory;