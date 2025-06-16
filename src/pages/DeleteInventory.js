import React, { useState } from "react";

const DeleteInventory = () => {
  const [form, setForm] = useState({ item_name: "", fabric_number: "" });

  const handleDelete = async () => {
    const response = await fetch("http://localhost:8080/AirtelAuth/inventory", {
      method: "POST",
      headers: { "Content-Type": "application/x-www-form-urlencoded" },
      body: new URLSearchParams({ action: "delete", ...form }),
    });

    const result = await response.json();
    alert(result.message);
  };

  return (
    <div>
      <h3>Delete Item</h3>
      <input type="text" placeholder="Item Name" onChange={(e) => setForm({ ...form, item_name: e.target.value })} />
      <input type="number" placeholder="Fabric Number" onChange={(e) => setForm({ ...form, fabric_number: e.target.value })} />
      <button onClick={handleDelete}>Delete Item</button>
    </div>
  );
};

export default DeleteInventory;
