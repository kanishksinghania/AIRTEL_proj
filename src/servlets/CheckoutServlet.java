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

@WebServlet("/checkout")
public class CheckoutServlet extends HttpServlet {

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

        String customerName = request.getParameter("customer_name");
        String day = request.getParameter("day");
        String month = request.getParameter("month");
        String year = request.getParameter("year");

        try (Connection conn = Create_DB.getConnection()) {
            StringBuilder query = new StringBuilder("SELECT * FROM checkout WHERE 1=1");

            if (year != null && !year.isEmpty()) query.append(" AND YEAR(purchase_date) = ?");
            if (month != null && !month.isEmpty()) query.append(" AND MONTH(purchase_date) = ?");
            if (day != null && !day.isEmpty()) query.append(" AND DAY(purchase_date) = ?");
            if (customerName != null && !customerName.isEmpty()) query.append(" AND customer_name LIKE ?");

            PreparedStatement stmt = conn.prepareStatement(query.toString());

            int paramIndex = 1;
            if (year != null && !year.isEmpty()) stmt.setInt(paramIndex++, Integer.parseInt(year));
            if (month != null && !month.isEmpty()) stmt.setInt(paramIndex++, Integer.parseInt(month));
            if (day != null && !day.isEmpty()) stmt.setInt(paramIndex++, Integer.parseInt(day));
            if (customerName != null && !customerName.isEmpty()) stmt.setString(paramIndex++, "%" + customerName + "%");

            ResultSet rs = stmt.executeQuery();
            JSONArray salesList = new JSONArray();

            while (rs.next()) {
                JSONObject record = new JSONObject();
                record.put("id", rs.getInt("id"));
                record.put("customer_name", rs.getString("customer_name"));
                record.put("item_name", rs.getString("item_name"));
                record.put("fabric_number", rs.getInt("fabric_number"));
                record.put("quantity_metres", rs.getDouble("quantity_metres"));
                record.put("price_per_metre", rs.getDouble("price_per_metre"));
                record.put("total_amount", rs.getDouble("total_amount"));
                record.put("payment_status", rs.getString("payment_status"));
                record.put("purchase_date", rs.getString("purchase_date"));
                salesList.put(record);
            }

            out.print(salesList.toString());

        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print("{\"error\": \"Error fetching sales records: " + e.getMessage() + "\"}");
        }
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        addCorsHeaders(response);
        PrintWriter out = response.getWriter();
        JSONObject jsonResponse = new JSONObject();

        try (Connection conn = Create_DB.getConnection()) {
            StringBuilder sb = new StringBuilder();
            String line;
            BufferedReader reader = request.getReader();
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
            JSONObject requestBody = new JSONObject(sb.toString());

            String customerName = requestBody.getString("customer_name");
            String paymentStatus = requestBody.getString("payment_status");
            String paymentType = requestBody.getString("payment_type");
            JSONArray items = requestBody.getJSONArray("items");

            for (int i = 0; i < items.length(); i++) {
                JSONObject item = items.getJSONObject(i);
                String itemName = item.getString("item_name");
                int fabricNumber = item.getInt("fabric_number");
                double quantityMetres = item.getDouble("quantity_metres");
                double pricePerMetre = item.getDouble("price_per_metre");

                String checkQuery = "SELECT quantity_metres FROM inventory WHERE item_name = ? AND fabric_number = ?";
                PreparedStatement checkStmt = conn.prepareStatement(checkQuery);
                checkStmt.setString(1, itemName);
                checkStmt.setInt(2, fabricNumber);
                ResultSet rs = checkStmt.executeQuery();

                if (rs.next()) {
                    double currentQuantity = rs.getDouble("quantity_metres");

                    if (currentQuantity < quantityMetres) {
                        jsonResponse.put("error", "Not enough stock available for " + itemName);
                        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                        out.print(jsonResponse.toString());
                        return;
                    }

                    String updateQuery = "UPDATE inventory SET quantity_metres = quantity_metres - ? WHERE item_name = ? AND fabric_number = ?";
                    PreparedStatement updateStmt = conn.prepareStatement(updateQuery);
                    updateStmt.setDouble(1, quantityMetres);
                    updateStmt.setString(2, itemName);
                    updateStmt.setInt(3, fabricNumber);
                    updateStmt.executeUpdate();

                    String insertQuery = "INSERT INTO checkout (customer_name, item_name, fabric_number, quantity_metres, price_per_metre, payment_status, payment_type) VALUES (?, ?, ?, ?, ?, ?, ?)";
                    PreparedStatement insertStmt = conn.prepareStatement(insertQuery);
                    insertStmt.setString(1, customerName);
                    insertStmt.setString(2, itemName);
                    insertStmt.setInt(3, fabricNumber);
                    insertStmt.setDouble(4, quantityMetres);
                    insertStmt.setDouble(5, pricePerMetre);
                    insertStmt.setString(6, paymentStatus);
                    insertStmt.setString(7, paymentType);

                    insertStmt.executeUpdate();
                } else {
                    jsonResponse.put("error", "Fabric not found for " + itemName);
                    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    out.print(jsonResponse.toString());
                    return;
                }
            }

            jsonResponse.put("message", "Checkout successful!");
            response.setStatus(HttpServletResponse.SC_OK);
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            jsonResponse.put("error", "Error processing checkout: " + e.getMessage());
        }

        out.print(jsonResponse.toString());
    }

    private void addCorsHeaders(HttpServletResponse response) {
        response.setHeader("Access-Control-Allow-Origin", "http://localhost:3000");
        response.setHeader("Access-Control-Allow-Methods", "GET, POST, OPTIONS");
        response.setHeader("Access-Control-Allow-Headers", "Content-Type, Authorization");
        response.setHeader("Access-Control-Allow-Credentials", "true");
    }
}

