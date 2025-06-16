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

@WebServlet("/check-credit")
public class CheckCreditServlet extends HttpServlet {

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

        try (Connection conn = Create_DB.getConnection()) {
            StringBuilder query = new StringBuilder("SELECT * FROM checkout WHERE payment_status = 'Credit'");
            PreparedStatement stmt;

            if (customerName != null && !customerName.isEmpty()) {
                query.append(" AND customer_name LIKE ?");
            }

            stmt = conn.prepareStatement(query.toString());

            if (customerName != null && !customerName.isEmpty()) {
                stmt.setString(1, "%" + customerName + "%");
            }

            ResultSet rs = stmt.executeQuery();
            JSONArray creditSalesList = new JSONArray();

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
                record.put("payment_type", rs.getString("payment_type"));
                creditSalesList.put(record);
            }

            out.print(creditSalesList.toString());

        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print("{\"error\": \"Error fetching credit sales: " + e.getMessage() + "\"}");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        addCorsHeaders(response);
        PrintWriter out = response.getWriter();

        try (Connection conn = Create_DB.getConnection()) {
            int checkoutId = Integer.parseInt(request.getParameter("id"));
            String paymentType = request.getParameter("payment_type");

            if (paymentType == null || paymentType.isEmpty()) {
                paymentType = "Cash";
            }

            String updateQuery = "UPDATE checkout SET payment_status = 'Paid', payment_type = ?, purchase_date = NOW() WHERE id = ?";
            PreparedStatement updateStmt = conn.prepareStatement(updateQuery);
            updateStmt.setString(1, paymentType);
            updateStmt.setInt(2, checkoutId);
            int rowsUpdated = updateStmt.executeUpdate();

            if (rowsUpdated > 0) {
                out.print("{\"message\": \"Payment status updated to Paid!\"}");
            } else {
                out.print("{\"error\": \"Failed to update payment status.\"}");
            }

        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print("{\"error\": \"Error updating payment status: " + e.getMessage() + "\"}");
        }
    }

    private void addCorsHeaders(HttpServletResponse response) {
        response.setHeader("Access-Control-Allow-Origin", "http://localhost:3000");
        response.setHeader("Access-Control-Allow-Methods", "GET, POST, OPTIONS");
        response.setHeader("Access-Control-Allow-Headers", "Content-Type, Authorization");
        response.setHeader("Access-Control-Allow-Credentials", "true");
    }
}
