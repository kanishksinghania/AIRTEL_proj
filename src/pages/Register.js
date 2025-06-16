import React, { useState } from "react";
import "./Register.css";

function Register() {
    const [user, setUser] = useState({
        fname: "",
        lname: "",
        email: "",
        password: "",
        age: "",
        phone: "",
        role: "user",
        address: "",
    });

    const [message, setMessage] = useState("");
    const [errors, setErrors] = useState({});

    const allowedDomains = ["airtel.com", "gmail.com", "yahoo.com", "apple.com"];

    const validateForm = () => {
        const newErrors = {};

        if (!/^[A-Za-z]+$/.test(user.fname)) {
            newErrors.fname = "First name should contain only alphabets.";
        }
        if (!/^[A-Za-z]+$/.test(user.lname)) {
            newErrors.lname = "Last name should contain only alphabets.";
        }

        const emailDomain = user.email.split("@")[1];
        if (!allowedDomains.includes(emailDomain)) {
            newErrors.email = "Email should be from @airtel.com, @gmail.com, @yahoo.com, or @apple.com.";
        }

        if (!/(?=.*[A-Z])(?=.*\d)/.test(user.password)) {
            newErrors.password = "Password must contain at least 1 uppercase letter and 1 number.";
        }

        if (!/^\d{10}$/.test(user.phone)) {
            newErrors.phone = "Phone number must be exactly 10 digits.";
        }

        setErrors(newErrors);
        return Object.keys(newErrors).length === 0;
    };

    const handleChange = (e) => {
        setUser({ ...user, [e.target.name]: e.target.value });
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        
        if (!validateForm()) {
            return;
        }

        const formData = new URLSearchParams();
        for (const key in user) {
            formData.append(key, user[key]);
        }
        formData.append("action", "register");

        console.log("Registering user:", user);

        try {
            const response = await fetch("http://localhost:8080/AirtelAuth/main", {
                method: "POST",
                headers: {
                    "Content-Type": "application/x-www-form-urlencoded",
                },
                body: formData,
            });

            const result = await response.json();
            console.log("Server response:", result);

            if (response.ok) {
                setMessage({ text: result.message, type: "success" });
            } else {
                setMessage({ text: result.error, type: "error" });
            }
        } catch (error) {
            setMessage({ text: `Error: ${error.message}`, type: "error" });
            console.error("Registration error:", error);
        }
    };

    return (
        <div className="register-container">
            <h2>Register</h2>
            <form onSubmit={handleSubmit} className="register-form">
                <input type="text" name="fname" placeholder="First Name" onChange={handleChange} value={user.fname} required />
                {errors.fname && <p className="error-message">{errors.fname}</p>}
                
                <input type="text" name="lname" placeholder="Last Name" onChange={handleChange} value={user.lname} required />
                {errors.lname && <p className="error-message">{errors.lname}</p>}
                
                <input type="email" name="email" placeholder="Email" onChange={handleChange} value={user.email} required />
                {errors.email && <p className="error-message">{errors.email}</p>}
                
                <input type="password" name="password" placeholder="Password" onChange={handleChange} value={user.password} required />
                {errors.password && <p className="error-message">{errors.password}</p>}
                
                <input type="number" name="age" placeholder="Age" onChange={handleChange} value={user.age} required />
                
                <input type="text" name="phone" placeholder="Phone" onChange={handleChange} value={user.phone} required />
                {errors.phone && <p className="error-message">{errors.phone}</p>}
                
                <input type="text" name="address" placeholder="Address" onChange={handleChange} value={user.address} required />
                
                <select name="role" onChange={handleChange} value={user.role}>
                    <option value="user">User</option>
                    <option value="admin">Admin</option>
                </select>
                
                <button type="submit" className="register-button">Register</button>
            </form>

            {message && (
                <p className={message.type === "success" ? "success-message" : "error-message"}>
                    {message.text}
                </p>
            )}
        </div>
    );
}

export default Register;
