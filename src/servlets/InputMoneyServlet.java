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

@WebServlet("/input-money")
public class InputMoneyServlet extends HttpServlet {

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
            String query = "SELECT * FROM input_money ORDER BY timestamp DESC";
            PreparedStatement stmt = conn.prepareStatement(query);
            ResultSet rs = stmt.executeQuery();

            JSONArray moneyList = new JSONArray();
            double totalAmount = 0; 

            while (rs.next()) {
                JSONObject record = new JSONObject();
                record.put("id", rs.getInt("id"));
                record.put("amount", rs.getDouble("amount"));
                record.put("purpose", rs.getString("purpose"));
                record.put("timestamp", rs.getString("timestamp"));
                moneyList.put(record);
                totalAmount += rs.getDouble("amount");
            }

            JSONObject result = new JSONObject();
            result.put("money_inputs", moneyList);
            result.put("total_amount", totalAmount);
            out.print(result.toString());

        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print("{\"error\": \"Error fetching money inputs: " + e.getMessage() + "\"}");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        addCorsHeaders(response);
        PrintWriter out = response.getWriter();

        try (Connection conn = Create_DB.getConnection()) {
            double amount = Double.parseDouble(request.getParameter("amount"));
            String purpose = request.getParameter("purpose");

            String insertQuery = "INSERT INTO input_money (amount, purpose) VALUES (?, ?)";
            PreparedStatement stmt = conn.prepareStatement(insertQuery);
            stmt.setDouble(1, amount);
            stmt.setString(2, purpose);

            int rowsInserted = stmt.executeUpdate();
            if (rowsInserted > 0) {
                out.print("{\"message\": \"Money input added successfully!\"}");
            } else {
                out.print("{\"error\": \"Failed to add money input.\"}");
            }

        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print("{\"error\": \"Error adding money input: " + e.getMessage() + "\"}");
        }
    }

    private void addCorsHeaders(HttpServletResponse response) {
        response.setHeader("Access-Control-Allow-Origin", "http://localhost:3000");
        response.setHeader("Access-Control-Allow-Methods", "GET, POST, OPTIONS");
        response.setHeader("Access-Control-Allow-Headers", "Content-Type, Authorization");
        response.setHeader("Access-Control-Allow-Credentials", "true");
    }
}
