import React, { useState } from "react";

const AddInventory = () => {
  const [form, setForm] = useState({ item_name: "", fabric_number: "", quantity_metres: "" });

  const handleSubmit = async () => {
    const response = await fetch("http://localhost:8080/AirtelAuth/inventory", {
      method: "POST",
      headers: { "Content-Type": "application/x-www-form-urlencoded" },
      body: new URLSearchParams({ action: "add", ...form }),
    });

    const result = await response.json();
    alert(result.message);
  };

  return (
    <div>
      <h3>Add New Item</h3>
      <input type="text" placeholder="Item Name" onChange={(e) => setForm({ ...form, item_name: e.target.value })} />
      <input type="number" placeholder="Fabric Number" onChange={(e) => setForm({ ...form, fabric_number: e.target.value })} />
      <input type="number" step="0.01" placeholder="Quantity (m)" onChange={(e) => setForm({ ...form, quantity_metres: e.target.value })} />
      <button onClick={handleSubmit}>Add Item</button>
    </div>
  );
};

export default AddInventory;
