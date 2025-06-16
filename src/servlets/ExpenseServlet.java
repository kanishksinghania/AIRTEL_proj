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

@WebServlet("/expenses")
public class ExpenseServlet extends HttpServlet {

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
            String query = "SELECT * FROM expenses ORDER BY updated_timestamp DESC";
            PreparedStatement stmt = conn.prepareStatement(query);
            ResultSet rs = stmt.executeQuery();

            JSONArray expenseList = new JSONArray();
            double totalExpenses = 0;

            while (rs.next()) {
                JSONObject expense = new JSONObject();
                expense.put("id", rs.getInt("id"));
                expense.put("description", rs.getString("description"));
                expense.put("expense_price", rs.getDouble("expense_price"));
                expense.put("updated_timestamp", rs.getString("updated_timestamp"));
                expenseList.put(expense);
                totalExpenses += rs.getDouble("expense_price");
            }

            JSONObject result = new JSONObject();
            result.put("expenses", expenseList);
            result.put("total_expenses", totalExpenses);
            out.print(result.toString());

        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print("{\"error\": \"Error fetching expenses: " + e.getMessage() + "\"}");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        addCorsHeaders(response);
        PrintWriter out = response.getWriter();

        try (Connection conn = Create_DB.getConnection()) {
            String description = request.getParameter("description");
            double expensePrice = Double.parseDouble(request.getParameter("expense_price"));

            String insertQuery = "INSERT INTO expenses (description, expense_price) VALUES (?, ?)";
            PreparedStatement insertStmt = conn.prepareStatement(insertQuery);
            insertStmt.setString(1, description);
            insertStmt.setDouble(2, expensePrice);

            int rowsInserted = insertStmt.executeUpdate();
            if (rowsInserted > 0) {
                out.print("{\"message\": \"Expense added successfully!\"}");
            } else {
                out.print("{\"error\": \"Failed to add expense.\"}");
            }

        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print("{\"error\": \"Error adding expense: " + e.getMessage() + "\"}");
        }
    }

    private void addCorsHeaders(HttpServletResponse response) {
        response.setHeader("Access-Control-Allow-Origin", "http://localhost:3000");
        response.setHeader("Access-Control-Allow-Methods", "GET, POST, OPTIONS");
        response.setHeader("Access-Control-Allow-Headers", "Content-Type, Authorization");
        response.setHeader("Access-Control-Allow-Credentials", "true");
    }
}
