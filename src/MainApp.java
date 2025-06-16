import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Scanner;

public class MainApp {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        
        System.out.println("Welcome to Airtel Auth System!");
        System.out.println("1) Create New Account");
        System.out.println("2) Login to Existing Account");
        System.out.println("3) Get Admin Role Details");
        System.out.println("4) Get User Role Details");
        System.out.println("5) Get All User Details");
        System.out.print("Choose an option: ");
        
        int choice = scanner.nextInt();
        scanner.nextLine(); 

        switch (choice) {
            case 1:
                UserRegistration.registerUser();
                break;
            case 2:
                UserLogin.loginUser();
                
                break;
            case 3:
                getAdminRoleDetails();
                break;
            case 4:
                getUserRoleDetails();
                break;
            case 5:
                getAllUserDetails();
                break;
            default:
                System.out.println("Invalid option, please try again.");
        }
        
        scanner.close();
    }

    private static void getAdminRoleDetails() {
        try (Connection conn = Create_DB.getConnection()) {
            String query = "SELECT fname, lname, email, phone, address FROM users WHERE role = 'admin'";
            PreparedStatement stmt = conn.prepareStatement(query);
            ResultSet rs = stmt.executeQuery();

            System.out.println("Admin Users:");
            while (rs.next()) {
                System.out.println("Name: " + rs.getString("fname") + " " + rs.getString("lname"));
                System.out.println("Email: " + rs.getString("email"));
                System.out.println("Phone: " + rs.getString("phone"));
                System.out.println("Address: " + rs.getString("address"));
                System.out.println("------------------------------");
            }
        } catch (Exception e) {
            System.out.println("Error fetching admin details: " + e.getMessage());
        }
    }

    private static void getUserRoleDetails() {
        try (Connection conn = Create_DB.getConnection()) {
            String query = "SELECT fname, lname, email, phone, address FROM users WHERE role = 'user'";
            PreparedStatement stmt = conn.prepareStatement(query);
            ResultSet rs = stmt.executeQuery();

            System.out.println("Regular Users:");
            while (rs.next()) {
                System.out.println("Name: " + rs.getString("fname") + " " + rs.getString("lname"));
                System.out.println("Email: " + rs.getString("email"));
                System.out.println("Phone: " + rs.getString("phone"));
                System.out.println("Address: " + rs.getString("address"));
                System.out.println("------------------------------");
            }
        } catch (Exception e) {
            System.out.println("Error fetching user details: " + e.getMessage());
        }
    }

    private static void getAllUserDetails() {
        try (Connection conn = Create_DB.getConnection()) {
            String query = "SELECT fname, lname, email, phone, role, address FROM users";
            PreparedStatement stmt = conn.prepareStatement(query);
            ResultSet rs = stmt.executeQuery();

            System.out.println("All Users:");
            while (rs.next()) {
                System.out.println("Name: " + rs.getString("fname") + " " + rs.getString("lname"));
                System.out.println("Email: " + rs.getString("email"));
                System.out.println("Phone: " + rs.getString("phone"));
                System.out.println("Role: " + rs.getString("role"));
                System.out.println("Address: " + rs.getString("address"));
                System.out.println("------------------------------");
            }
        } catch (Exception e) {
            System.out.println("Error fetching all user details: " + e.getMessage());
        }
    }
}
