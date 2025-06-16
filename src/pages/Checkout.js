import React, { useState, useEffect, useRef } from "react";
import './Checkout.css';
import logo from './logo.png'; // RF logo

const Checkout = () => {
  const [items, setItems] = useState([]); 
  const [fabricNumbers, setFabricNumbers] = useState([]); 
  const [selectedItems, setSelectedItems] = useState([]);
  const [slipNumber, setSlipNumber] = useState(null);
  const printRef = useRef();

  const [form, setForm] = useState({
    customer_name: "",
    item_name: "",
    fabric_number: "",
    quantity_metres: "",
    price_per_metre: "",
    total_amount: 0,
    payment_status: "Paid",  
    payment_type: "Cash",
  });

  useEffect(() => {
    fetch("http://localhost:8080/AirtelAuth/inventory")
      .then((response) => response.json())
      .then((data) => {
        const uniqueItems = [...new Set(data.map((item) => item.item_name))];
        setItems(uniqueItems);
      })
      .catch((error) => console.error("Error fetching inventory:", error));
  }, []);

  useEffect(() => {
    if (form.item_name) {
      fetch(`http://localhost:8080/AirtelAuth/inventory?item_name=${form.item_name}`)
        .then((response) => response.json())
        .then((data) => setFabricNumbers(data.map((item) => item.fabric_number)))
        .catch((error) => console.error("Error fetching fabric numbers:", error));
    } else {
      setFabricNumbers([]); 
    }
  }, [form.item_name]);

  const handleChange = (e) => {
    const { name, value } = e.target;
    let updatedForm = { ...form, [name]: value };

    if (name === "quantity_metres" || name === "price_per_metre") {
      updatedForm.total_amount = (parseFloat(updatedForm.quantity_metres || 0) * parseFloat(updatedForm.price_per_metre || 0)).toFixed(2);
    }

    setForm(updatedForm);
  };

  const handleAddItem = () => {
    if (!form.item_name || !form.fabric_number || !form.quantity_metres || !form.price_per_metre) {
      alert("Please fill in all fields before adding an item.");
      return;
    }

    setSelectedItems([...selectedItems, form]);

    setForm({
      ...form,
      item_name: "",
      fabric_number: "",
      quantity_metres: "",
      price_per_metre: "",
      total_amount: 0,
    });
  };

  const handleRemoveItem = (index) => {
    const updatedItems = [...selectedItems];
    updatedItems.splice(index, 1);
    setSelectedItems(updatedItems);
  };

  const totalPurchaseAmount = selectedItems.reduce((sum, item) => sum + parseFloat(item.total_amount), 0) + parseFloat(form.total_amount || 0);

  const handleCheckoutAndPrint = async (e) => {
    e.preventDefault();

    const checkoutItems = [...selectedItems, form].filter(item => item.item_name !== "");

    if (checkoutItems.length === 0) {
      alert("Please add at least one item for checkout.");
      return;
    }

    const requestData = {
      customer_name: form.customer_name,
      payment_status: form.payment_status,
      payment_type: form.payment_type,
      items: checkoutItems,
    };

    const response = await fetch("http://localhost:8080/AirtelAuth/checkout", {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify(requestData),
    });

    if (response.ok) {
      const responseData = await response.json();
      if (Array.isArray(responseData) && responseData.length > 0) {
        setSlipNumber(responseData[0].id); // ✅ Extract slip number from first item
      }
      

      alert("Checkout successful!");
      window.print();
      setForm({
        customer_name: "",
        item_name: "",
        fabric_number: "",
        quantity_metres: "",
        price_per_metre: "",
        total_amount: 0,
        payment_status: "Paid",
        payment_type: "Cash",
      });
      setSelectedItems([]);
    } else {
      alert("Checkout failed!");
    }
  };

  return (
    <div className="checkout-container" ref={printRef}>
      {/* Header with RF Logo & Date */}
      <div className="receipt-header">
        <div className="store-name">ROYAL FABRICS</div>
        <img src={logo} alt="RF Logo" className="receipt-logo" />
      </div>

      {/* ✅ Slip Number Added */}
      {slipNumber && <p className="receipt-slip">Slip No: {slipNumber}</p>}

      <p className="receipt-date">{new Date().toLocaleDateString('en-GB')}</p>

      {/* Customer Name & Payment Details */}
      <table className="checkout-table">
        <tbody>
          <tr>
            <td><strong>Customer Name:</strong></td>
            <td><input type="text" name="customer_name" value={form.customer_name} onChange={handleChange} required /></td>
          </tr>
          <tr>
            <td><strong>Payment Type:</strong></td>
            <td>
              <select name="payment_type" value={form.payment_type} onChange={handleChange} required>
                <option value="Cash">Cash</option>
                <option value="UPI">UPI</option>
                <option value="Bank">Bank</option>
              </select>
            </td>
          </tr>
          <tr>
            <td><strong>Payment Status:</strong></td>
            <td>
              <select name="payment_status" value={form.payment_status} onChange={handleChange} required>
                <option value="Paid">Paid</option>
                <option value="Credit">Credit</option>
              </select>
            </td>
          </tr>
        </tbody>
      </table>

      {/* Items Table */}
      <table className="checkout-table">
        <thead>
          <tr>
            <th>S.No</th>
            <th>Item Name</th>
            <th>Fabric No</th>
            <th>Quantity (m)</th>
            <th>Price/m (₹)</th>
            <th>Total (₹)</th>
            <th>Action</th>
          </tr>
        </thead>
        <tbody>
          {selectedItems.map((item, index) => (
            <tr key={index}>
              <td>{index + 1}</td>
              <td>{item.item_name}</td>
              <td>{item.fabric_number}</td>
              <td>{item.quantity_metres}</td>
              <td>₹{item.price_per_metre}</td>
              <td>₹{item.total_amount}</td>
              <td><button className="remove-item-btn" onClick={() => handleRemoveItem(index)}>❌</button></td>
            </tr>
          ))}
          <tr>
            <td>{selectedItems.length + 1}</td>
            <td>
              <select name="item_name" value={form.item_name} onChange={handleChange} required>
                <option value="">Select Item</option>
                {items.map((item, index) => (
                  <option key={index} value={item}>{item}</option>
                ))}
              </select>
            </td>
            <td>
              <select name="fabric_number" value={form.fabric_number} onChange={handleChange} required>
                <option value="">Select Fabric</option>
                {fabricNumbers.map((fabric, index) => (
                  <option key={index} value={fabric}>{fabric}</option>
                ))}
              </select>
            </td>
            <td><input type="number" name="quantity_metres" value={form.quantity_metres} onChange={handleChange} required /></td>
            <td><input type="number" name="price_per_metre" value={form.price_per_metre} onChange={handleChange} required /></td>
            <td>₹{form.total_amount}</td>
            <td><button className="add-item-btn" onClick={handleAddItem}>➕</button></td>
          </tr>
        </tbody>
      </table>

      <button className="checkout-btn" onClick={handleCheckoutAndPrint}>✔ Checkout & Print</button>

      <h3 className="total-amount">Total: ₹{totalPurchaseAmount.toFixed(2)}</h3>
    </div>
  );
};

export default Checkout;