import React, { useState, useEffect } from "react";
import './AdminDetails.css'; // Import the CSS file

const AdminDetails = () => {
  const [admins, setAdmins] = useState([]);

  useEffect(() => {
    fetch("http://localhost:8080/AirtelAuth/main?action=admins") 
      .then((response) => response.json())
      .then((data) => setAdmins(data))
      .catch((error) => console.error("Error fetching admin details:", error));
  }, []);

  return (
    <div className="admin-details-container">
      <h1>Admin Users</h1>
      <table>
        <thead>
          <tr>
            <th>Name</th>
            <th>Email</th>
            <th>Phone</th>
            <th>Address</th>
          </tr>
        </thead>
        <tbody>
          {admins.map((admin, index) => (
            <tr key={index}>
              <td>{admin.name}</td>
              <td>{admin.email}</td>
              <td>{admin.phone}</td>
              <td>{admin.address}</td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
};

export default AdminDetails;