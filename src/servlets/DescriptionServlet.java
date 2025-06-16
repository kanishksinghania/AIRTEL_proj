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
import org.json.JSONObject;
import db.Create_DB;

@WebServlet("/description")
public class DescriptionServlet extends HttpServlet {

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
            JSONObject result = new JSONObject();

            String totalSalesQuery = "SELECT COALESCE(SUM(total_amount), 0) AS total_sales FROM checkout WHERE DATE(purchase_date) = CURDATE() AND payment_status = 'Paid'";
            PreparedStatement stmt1 = conn.prepareStatement(totalSalesQuery);
            ResultSet rs1 = stmt1.executeQuery();
            if (rs1.next()) {
                result.put("total_sales", rs1.getDouble("total_sales"));
            }

            String upiBankSalesQuery = "SELECT COALESCE(SUM(total_amount), 0) AS upi_bank_sales FROM checkout WHERE DATE(purchase_date) = CURDATE() AND payment_status = 'Paid' AND (payment_type = 'UPI' OR payment_type = 'Bank')";
            PreparedStatement stmt2 = conn.prepareStatement(upiBankSalesQuery);
            ResultSet rs2 = stmt2.executeQuery();
            if (rs2.next()) {
                result.put("upi_bank_sales", rs2.getDouble("upi_bank_sales"));
            }

            String expensesQuery = "SELECT COALESCE(SUM(expense_price), 0) AS total_expenses FROM expenses WHERE DATE(updated_timestamp) = CURDATE()";
            PreparedStatement stmt3 = conn.prepareStatement(expensesQuery);
            ResultSet rs3 = stmt3.executeQuery();
            if (rs3.next()) {
                result.put("total_expenses", rs3.getDouble("total_expenses"));
            }

            String creditSalesQuery = "SELECT COALESCE(SUM(total_amount), 0) AS credit_sales FROM checkout WHERE DATE(purchase_date) = CURDATE() AND payment_status = 'Credit'";
            PreparedStatement stmt4 = conn.prepareStatement(creditSalesQuery);
            ResultSet rs4 = stmt4.executeQuery();
            if (rs4.next()) {
                result.put("credit_sales", rs4.getDouble("credit_sales"));
            }

            String moneyQuery = "SELECT COALESCE(SUM(amount), 0) AS party_entry FROM input_money WHERE DATE(timestamp) = CURDATE()";
            PreparedStatement stmt5 = conn.prepareStatement(moneyQuery);
            ResultSet rs5 = stmt5.executeQuery();
            if (rs5.next()) {
                result.put("party_entry", rs5.getDouble("party_entry"));
            }

            out.print(result.toString());

        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print("{\"error\": \"Error fetching description data: " + e.getMessage() + "\"}");
        }
    }

    private void addCorsHeaders(HttpServletResponse response) {
        response.setHeader("Access-Control-Allow-Origin", "http://localhost:3000");
        response.setHeader("Access-Control-Allow-Methods", "GET, POST, OPTIONS");
        response.setHeader("Access-Control-Allow-Headers", "Content-Type, Authorization");
        response.setHeader("Access-Control-Allow-Credentials", "true");
    }
}
