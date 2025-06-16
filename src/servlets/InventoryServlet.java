package servlets;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.json.JSONArray;
import org.json.JSONObject;
import db.Create_DB;

@WebServlet("/inventory")
public class InventoryServlet extends HttpServlet {

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

        String itemName = request.getParameter("item_name");

        try (Connection conn = Create_DB.getConnection()) {
            String query;
            PreparedStatement stmt;

            if (itemName != null && !itemName.isEmpty()) {
                query = "SELECT * FROM inventory WHERE item_name = ?";
                stmt = conn.prepareStatement(query);
                stmt.setString(1, itemName);
            } else {
                query = "SELECT * FROM inventory";
                stmt = conn.prepareStatement(query);
            }

            ResultSet rs = stmt.executeQuery();
            JSONArray itemList = new JSONArray();

            while (rs.next()) {
                JSONObject item = new JSONObject();
                item.put("id", rs.getInt("id"));
                item.put("item_name", rs.getString("item_name"));
                item.put("fabric_number", rs.getInt("fabric_number"));
                item.put("quantity_metres", rs.getDouble("quantity_metres")); // ✅ Fix: Use Double for decimal support
                item.put("created_at", rs.getString("created_at"));
                item.put("updated_at", rs.getString("updated_at"));
                itemList.put(item);
            }

            out.print(itemList.toString());

        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print("{\"error\": \"Error fetching inventory: " + e.getMessage() + "\"}");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        addCorsHeaders(response);
        PrintWriter out = response.getWriter();

        String action = request.getParameter("action");

        try (Connection conn = Create_DB.getConnection()) {

            if ("add".equalsIgnoreCase(action)) {
                String itemName = request.getParameter("item_name");
                int fabricNumber = Integer.parseInt(request.getParameter("fabric_number"));
                double quantityMetres = Double.parseDouble(request.getParameter("quantity_metres")); // ✅ Fix: Allow decimal
                Timestamp timestamp = new Timestamp(System.currentTimeMillis());

                String query = "INSERT INTO inventory (item_name, fabric_number, quantity_metres, created_at, updated_at) VALUES (?, ?, ?, ?, ?)";
                PreparedStatement stmt = conn.prepareStatement(query);
                stmt.setString(1, itemName);
                stmt.setInt(2, fabricNumber);
                stmt.setDouble(3, quantityMetres); // ✅ Fix: Use setDouble()
                stmt.setTimestamp(4, timestamp);
                stmt.setTimestamp(5, timestamp);

                int rowsInserted = stmt.executeUpdate();
                if (rowsInserted > 0) {
                    out.print("{\"message\": \"Item added successfully!\"}");
                } else {
                    out.print("{\"error\": \"Failed to add item.\"}");
                }

            } else if ("update".equalsIgnoreCase(action)) {
                String itemName = request.getParameter("item_name");
                int fabricNumber = Integer.parseInt(request.getParameter("fabric_number"));
                double quantityChange = Double.parseDouble(request.getParameter("quantity_metres")); // ✅ Fix: Allow decimal input
                String operation = request.getParameter("operation"); // "add" or "reduce"

                String selectQuery = "SELECT quantity_metres FROM inventory WHERE item_name = ? AND fabric_number = ?";
                PreparedStatement selectStmt = conn.prepareStatement(selectQuery);
                selectStmt.setString(1, itemName);
                selectStmt.setInt(2, fabricNumber);
                ResultSet rs = selectStmt.executeQuery();

                if (rs.next()) {
                    double currentQuantity = rs.getDouble("quantity_metres"); // ✅ Fix: Use getDouble()
                    double newQuantity = "reduce".equalsIgnoreCase(operation) ? (currentQuantity - quantityChange) : (currentQuantity + quantityChange);
                    if (newQuantity < 0) newQuantity = 0;

                    String updateQuery = "UPDATE inventory SET quantity_metres = ?, updated_at = ? WHERE item_name = ? AND fabric_number = ?";
                    PreparedStatement updateStmt = conn.prepareStatement(updateQuery);
                    updateStmt.setDouble(1, newQuantity); // ✅ Fix: Use setDouble()
                    updateStmt.setTimestamp(2, new Timestamp(System.currentTimeMillis()));
                    updateStmt.setString(3, itemName);
                    updateStmt.setInt(4, fabricNumber);

                    int rowsUpdated = updateStmt.executeUpdate();
                    if (rowsUpdated > 0) {
                        out.print("{\"message\": \"Item quantity updated successfully!\"}");
                    } else {
                        out.print("{\"error\": \"Failed to update item.\"}");
                    }
                } else {
                    out.print("{\"error\": \"Item not found!\"}");
                }

            } else if ("delete".equalsIgnoreCase(action)) {
                String itemName = request.getParameter("item_name");
                int fabricNumber = Integer.parseInt(request.getParameter("fabric_number"));

                String query = "DELETE FROM inventory WHERE item_name = ? AND fabric_number = ?";
                PreparedStatement stmt = conn.prepareStatement(query);
                stmt.setString(1, itemName);
                stmt.setInt(2, fabricNumber);

                int rowsDeleted = stmt.executeUpdate();
                if (rowsDeleted > 0) {
                    out.print("{\"message\": \"Item deleted successfully!\"}");
                } else {
                    out.print("{\"error\": \"Failed to delete item.\"}");
                }
            } else {
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
