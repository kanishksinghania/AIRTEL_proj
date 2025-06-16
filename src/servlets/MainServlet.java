package servlets;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.json.JSONArray;
import org.json.JSONObject;
import db.Create_DB;

@WebServlet("/main")
public class MainServlet extends HttpServlet {


    @Override
    protected void doOptions(HttpServletRequest request, HttpServletResponse response) {
        addCorsHeaders(response);
        response.setStatus(HttpServletResponse.SC_OK);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        addCorsHeaders(response);
        PrintWriter out = response.getWriter();

        String action = request.getParameter("action");

        try (Connection conn = Create_DB.getConnection()) {
            String query;

            if ("admins".equalsIgnoreCase(action)) {
                query = "SELECT fname, lname, email, phone, role, address FROM users WHERE role = 'admin'";
            } else if ("users".equalsIgnoreCase(action)) {
                query = "SELECT fname, lname, email, phone, role, address FROM users WHERE role = 'user'";
            } else if ("all-users".equalsIgnoreCase(action)) {
                query = "SELECT fname, lname, email, phone, role, address FROM users";            
            } else {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.print("{\"error\": \"Invalid action parameter\"}");
                return;
            }

            PreparedStatement stmt = conn.prepareStatement(query);
            ResultSet rs = stmt.executeQuery();
            JSONArray userList = new JSONArray();

            while (rs.next()) {
                JSONObject user = new JSONObject();
                user.put("name", rs.getString("fname") + " " + rs.getString("lname"));
                user.put("email", rs.getString("email"));
                user.put("phone", rs.getString("phone"));
                user.put("role", rs.getString("role"));
                user.put("address", rs.getString("address"));
                userList.put(user);
            }

            out.print(userList.toString());

        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print("{\"error\": \"Error fetching user details: " + e.getMessage() + "\"}");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        addCorsHeaders(response);
        PrintWriter out = response.getWriter();

        String action = request.getParameter("action");

        try (Connection conn = Create_DB.getConnection()) {

            if ("register".equalsIgnoreCase(action)) {
                String fname = request.getParameter("fname");
                String lname = request.getParameter("lname");
                String email = request.getParameter("email");
                String password = request.getParameter("password");
                int age = Integer.parseInt(request.getParameter("age"));  
                String phone = request.getParameter("phone");
                String role = request.getParameter("role");
                String address = request.getParameter("address");

                String query = "INSERT INTO users (fname, lname, email, password, age, phone, role, address) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
                PreparedStatement stmt = conn.prepareStatement(query);
                stmt.setString(1, fname);
                stmt.setString(2, lname);
                stmt.setString(3, email);
                stmt.setString(4, password);
                stmt.setInt(5, age);
                stmt.setString(6, phone);
                stmt.setString(7, role);
                stmt.setString(8, address);

                int rowsInserted = stmt.executeUpdate();
                if (rowsInserted > 0) {
                    out.print("{\"message\": \"User registered successfully!\"}");
                } else {
                    out.print("{\"error\": \"Registration failed.\"}");
                }

            } else if ("login".equalsIgnoreCase(action)) {
                String email = request.getParameter("email");
                String password = request.getParameter("password");
            
                System.out.println("Login request received - Email: " + email + ", Password: " + password); // Debugging log
            
                String query = "SELECT fname, lname, email, password, age, phone, role, address FROM users WHERE email = ?";
                PreparedStatement stmt = conn.prepareStatement(query);
                stmt.setString(1, email);
                ResultSet rs = stmt.executeQuery();
            
                if (rs.next()) {
                    String storedPassword = rs.getString("password");
                    System.out.println("Stored password in DB: " + storedPassword); // Debugging log
            
                    if (storedPassword.equals(password)) {
                        JSONObject user = new JSONObject();
                        user.put("name", rs.getString("fname") + " " + rs.getString("lname"));
                        user.put("email", rs.getString("email"));
                        user.put("role", rs.getString("role"));
                        if (rs.getString("age") != null) { 
                            user.put("age", rs.getInt("age"));
                        } else {
                            user.put("age", "N/A"); // Default if missing
                        }
                        
                        if (rs.getString("address") != null) {
                            user.put("address", rs.getString("address"));
                        } else {
                            user.put("address", "N/A");
                        }                        
                        user.put("message", "Login successful!");
                        out.print(user.toString());
                    } else {
                        System.out.println("Password mismatch!"); // Debugging log
                        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                        out.print("{\"error\": \"Invalid password.\"}");
                    }
                } else {
                    System.out.println("Email not found in database!"); // Debugging log
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    out.print("{\"error\": \"Email not found.\"}");
                }
            }
            
             else {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.print("{\"error\": \"Invalid action parameter\"}");
            }

        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print("{\"error\": \"Error processing request: " + e.getMessage() + "\"}");
        }
    }

    private void addCorsHeaders(HttpServletResponse response) {
        response.setHeader("Access-Control-Allow-Origin", "http://localhost:3000");
        response.setHeader("Access-Control-Allow-Methods", "GET, POST, OPTIONS");
        response.setHeader("Access-Control-Allow-Headers", "Content-Type, Authorization");
        response.setHeader("Access-Control-Allow-Credentials", "true");
    }
}
