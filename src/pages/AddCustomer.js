import React, { useState } from "react";
import "./AddCustomer.css"; // Import styles

const AddCustomer = () => {
  const [customerName, setCustomerName] = useState("");
  const [phoneNumber, setPhoneNumber] = useState("");
  const [address, setAddress] = useState("");
  const [fileName, setFileName] = useState("");
  const [price, setPrice] = useState("");
  const [files, setFiles] = useState([]); // Stores multiple files with prices
  const [message, setMessage] = useState("");

  // ✅ Handle Adding Files to List
  const handleAddFile = () => {
    if (!fileName || !price) {
      alert("Please enter file name and price.");
      return;
    }

    // Add new file to list
    setFiles([...files, { fileName, price }]);

    // Reset file name and price fields
    setFileName("");
    setPrice("");
  };

  // ✅ Handle Form Submission (Add Customer with Files)
  const handleSubmit = async (e) => {
    e.preventDefault();

    if (!customerName || !phoneNumber || !address || files.length === 0) {
        alert("Please fill all fields and add at least one file.");
        return;
    }

    const requestData = {
        customer_name: customerName,
        phone_number: phoneNumber,
        address: address,
        files: files.map(file => ({
            fileName: file.fileName,  // Ensure correct key names
            price: parseFloat(file.price) // Convert price to number
        }))
    };

    try {
        const response = await fetch("http://localhost:8080/AirtelAuth/filecustomer", {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify(requestData),
        });

        const result = await response.json();
        setMessage(result.message || result.error);

        if (response.ok) {
            // Reset form after successful submission
            setCustomerName("");
            setPhoneNumber("");
            setAddress("");
            setFiles([]); // Clear added files
        }
    } catch (error) {
        console.error("Error adding customer:", error);
        setMessage("Failed to add customer.");
    }
};


  return (
    <div className="add-customer-container">
      <h2>➕ Add Customer</h2>

      {message && <p className="message">{message}</p>}

      <form className="customer-form">
        {/* Customer Name */}
        <input
          type="text"
          placeholder="Customer Name"
          value={customerName}
          onChange={(e) => setCustomerName(e.target.value)}
          required
        />

        {/* Phone Number */}
        <input
          type="text"
          placeholder="Phone Number"
          value={phoneNumber}
          onChange={(e) => setPhoneNumber(e.target.value)}
          required
        />

        {/* Address */}
        <input
          type="text"
          placeholder="Address"
          value={address}
          onChange={(e) => setAddress(e.target.value)}
          required
        />

        <div className="file-input-container">
          {/* File Name Input */}
          <input
            type="text"
            placeholder="File Name"
            value={fileName}
            onChange={(e) => setFileName(e.target.value)}
          />

          {/* Price Input */}
          <input
            type="number"
            placeholder="Price (₹)"
            value={price}
            onChange={(e) => setPrice(e.target.value)}
          />

          {/* Add File Button */}
          <button type="button" onClick={handleAddFile} className="add-file-btn">
            ➕ Add File
          </button>
        </div>

        {/* Display Added Files */}
        {files.length > 0 && (
          <div className="file-list">
            <h3>📂 Files Added:</h3>
            <ul>
              {files.map((file, index) => (
                <li key={index}>
                  {file.fileName} - ₹{file.price}
                </li>
              ))}
            </ul>
          </div>
        )}

        {/* Submit Button */}
        <button type="submit" onClick={handleSubmit} className="submit-btn">
          ✔ Add Customer
        </button>
      </form>
    </div>
  );
};

export default AddCustomer;

