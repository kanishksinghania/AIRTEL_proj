package servlets;

import java.io.BufferedReader;
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

@WebServlet("/filecustomer")
public class FileServlet extends HttpServlet {

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

        try (Connection conn = Create_DB.getConnection()) {
            String query = "SELECT * FROM filecustomer ORDER BY customer_name ASC";
            PreparedStatement stmt = conn.prepareStatement(query);
            ResultSet rs = stmt.executeQuery();

            JSONArray customerList = new JSONArray();
            while (rs.next()) {
                JSONObject record = new JSONObject();
                record.put("id", rs.getInt("id"));
                record.put("customer_name", rs.getString("customer_name"));
                record.put("file_name", rs.getString("file_name"));
                record.put("price", rs.getDouble("price"));
                record.put("phone_number", rs.getString("phone_number"));
                record.put("address", rs.getString("address"));
                customerList.put(record);
            }

            out.print(customerList.toString());

        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print("{\"error\": \"Error fetching customers: " + e.getMessage() + "\"}");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        addCorsHeaders(response);
        PrintWriter out = response.getWriter();
        
        try (Connection conn = Create_DB.getConnection()) {
            StringBuilder jsonBuffer = new StringBuilder();
            String line;
            BufferedReader reader = request.getReader();
            while ((line = reader.readLine()) != null) {
                jsonBuffer.append(line);
            }

            JSONObject jsonData = new JSONObject(jsonBuffer.toString());
            String customerName = jsonData.getString("customer_name");
            String phoneNumber = jsonData.getString("phone_number");
            String address = jsonData.getString("address");
            JSONArray filesArray = jsonData.getJSONArray("files");

            String insertQuery = "INSERT INTO filecustomer (customer_name, file_name, price, phone_number, address) VALUES (?, ?, ?, ?, ?)";
            PreparedStatement stmt = conn.prepareStatement(insertQuery);

            for (int i = 0; i < filesArray.length(); i++) {
                JSONObject fileObj = filesArray.getJSONObject(i);
                String fileName = fileObj.getString("fileName");
                double price = fileObj.getDouble("price");

                stmt.setString(1, customerName);
                stmt.setString(2, fileName);
                stmt.setDouble(3, price);
                stmt.setString(4, phoneNumber);
                stmt.setString(5, address);
                stmt.addBatch();
            }

            int[] rowsInserted = stmt.executeBatch();

            if (rowsInserted.length > 0) {
                out.print("{\"message\": \"Customer added successfully with files!\"}");
            } else {
                out.print("{\"error\": \"Failed to add customer.\"}");
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
