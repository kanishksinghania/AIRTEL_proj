import React from "react";
import { BrowserRouter as Router, Routes, Route } from "react-router-dom";
import Homepage from "./pages/Homepage";
import Inventory from "./pages/Inventory";
import Home from "./pages/Home";
import Register from "./pages/Register";
import Login from "./pages/Login";
import AdminDetails from "./pages/AdminDetails";
import UserDetails from "./pages/UserDetails";
import AllUsers from "./pages/AllUsers";
import LoginPage from "./pages/LoginPage";
import Checkout from "./pages/Checkout";
import CheckSales from "./pages/CheckSales";
import CheckCredit from "./pages/CheckCredit";
import AddCustomer from "./pages/AddCustomer";
import ViewCustomer from "./pages/ViewCustomer";
import Expense from "./pages/Expense";
import TodaysDescription from "./pages/TodaysDescription";
import InputMoney from "./pages/InputMoney";
import PrintReceipt from "./pages/PrintReceipt";
import NotFound from "./pages/NotFound"; 

const App = () => {
  return (
    <Router>
      <Routes>
        <Route path="/" element={<Homepage />} />
        <Route path="/inventory" element={<Inventory />} />
        <Route path="/checkout" element={<Checkout />} />  
        <Route path="/check-sales" element={<CheckSales />} />
        <Route path="/check-credit" element={<CheckCredit />} />
        <Route path="/add-customer" element={<AddCustomer />} />
        <Route path="/view-customers" element={<ViewCustomer />} />
        <Route path="/expense" element={<Expense />} />
        <Route path="/printreceipt" element={<PrintReceipt />} />
        <Route path="/todays-description" element={<TodaysDescription />} />
        <Route path="/user-management" element={<Home />} />
        <Route path="/register" element={<Register />} />
        <Route path="/login" element={<Login />} />
        <Route path="/admin-details" element={<AdminDetails />} />
        <Route path="/user-details" element={<UserDetails />} />
        <Route path="/all-users" element={<AllUsers />} />
        <Route path="/login-success" element={<LoginPage />} />
        <Route path="/input-money" element={<InputMoney />} />
        <Route path="*" element={<NotFound />} /> {/* Catch-all for 404 */}
      </Routes>
    </Router>
  );
};

export default App;