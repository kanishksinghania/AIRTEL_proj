import React, { useRef } from "react";
import "./PrintReceipt.css";
import logo from "./logo.png";

const PrintReceipt = ({ data, onClose }) => {
  const printRef = useRef();

  if (!data || !data.customer_name) {
    return <p style={{ color: "red" }}>Error: Missing data for receipt.</p>;
  }

  const handlePrint = () => {
    window.print();
  };

  return (
    <div className="receipt-overlay">
      <div className="receipt-container" ref={printRef}>
        {/* Header with Logo & Store Name */}
        <div className="receipt-header">
          <div className="store-name">Royal Fabrics</div>
          <img src={logo} alt="RF Logo" className="receipt-logo" />
        </div>

        {/* Customer & Payment Details */}
        <div className="receipt-info">
          <p><strong>Issued To:</strong> {data.customer_name}</p>
          <p><strong>Date:</strong> {new Date().toLocaleDateString()}</p>
          <p><strong>Payment Status:</strong> {data.payment_status}</p>
          <p><strong>Payment Type:</strong> {data.payment_type}</p>
        </div>

        {/* Items Table */}
        <table className="receipt-table">
          <thead>
            <tr>
              <th>S.No</th>
              <th>Item Name</th>
              <th>Fabric No</th>
              <th>Qty (m)</th>
              <th>Rate/m (₹)</th>
              <th>Total (₹)</th>
            </tr>
          </thead>
          <tbody>
            {data.items.map((item, index) => (
              <tr key={index}>
                <td>{index + 1}</td>
                <td>{item.item_name}</td>
                <td>{item.fabric_number}</td>
                <td>{item.quantity_metres}</td>
                <td>₹{item.price_per_metre}</td>
                <td>₹{item.total_amount}</td>
              </tr>
            ))}
          </tbody>
        </table>

        {/* Total Amount */}
        <div className="receipt-total">
          <strong>Total Amount: ₹{data.items.reduce((sum, item) => sum + parseFloat(item.total_amount), 0)}</strong>
        </div>

        {/* Print & Close Buttons */}
        <div className="receipt-actions">
          <button onClick={handlePrint} className="print-btn">🖨 Print</button>
          <button onClick={onClose} className="close-btn">❌ Close</button>
        </div>
      </div>
    </div>
  );
};

export default PrintReceipt;
