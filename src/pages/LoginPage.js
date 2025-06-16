import React from "react";
import { useLocation } from "react-router-dom";
import './LoginPage.css'; // Import the CSS file

const LoginPage = () => {
  const location = useLocation();
  const user = location.state?.user || JSON.parse(localStorage.getItem("user"));

  return (
    <div className="login-page-container">
      <h2>User Successfully Logged In ✅</h2>
      {user ? (
        <table className="user-info-table">
          <tbody>
            <tr>
              <td><strong>Name:</strong></td>
              <td>{user.name}</td>
            </tr>
            <tr>
              <td><strong>Age:</strong></td>
              <td>{user.age}</td>
            </tr>
            <tr>
              <td><strong>Email ID:</strong></td>
              <td>{user.email}</td>
            </tr>
            <tr>
              <td><strong>Address:</strong></td>
              <td>{user.address}</td>
            </tr>
          </tbody>
        </table>
      ) : (
        <p>No user data found!</p>
      )}
    </div>
  );
};

export default LoginPage;