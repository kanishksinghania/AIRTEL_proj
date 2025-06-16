import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Scanner;

public class UserLogin {
    public static void loginUser() {
        Scanner scanner = new Scanner(System.in);
        
        System.out.print("Enter email: ");
        String username = scanner.nextLine();
        System.out.print("Enter password: ");
        String password = scanner.nextLine();

        try (Connection conn = Create_DB.getConnection()) {
            String sql = "SELECT * FROM users WHERE email = ? AND password = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, username);
            stmt.setString(2, password);

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                System.out.println("Login successful! Welcome " + rs.getString("fname"));
            } else {
                System.out.println("Invalid credentials.");
                return;
            }

            System.out.println("Welcome to your Airtel Account!");
            System.out.println("1. Show your info");
            System.out.println("2. Browse Plans!");
            System.out.println("3. Update your details");
            System.out.println("4. Delete your account");
            System.out.print("Choose an option: ");

            int choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {
                case 1:
                    displayUserInfo(username);
                    break;
                case 2:
                    browsePlans.plans();
                    break;
                case 3:
                    UserUpdate.updateUser();
                    break;
                case 4:
                    UserDelete.deleteUser();
                    break;
                default:
                    System.out.println("Invalid option, please try again.");
            }
        } catch (Exception e) {
            System.out.println("Error during login: " + e.getMessage());
        }
    }

    private static void displayUserInfo(String email) {
        try (Connection conn = Create_DB.getConnection()) {
            String query = "SELECT * FROM users WHERE email = ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, email);
    
            ResultSet rs = stmt.executeQuery();
         
            if (rs.next()) {
                System.out.println("User Info:");
                System.out.println("First Name: " + rs.getString("fname"));
                System.out.println("Last Name: " + rs.getString("lname"));
                System.out.println("Email: " + rs.getString("email"));
                System.out.println("Phone/Mobile: " + rs.getString("phone"));
                System.out.println("Role: " + rs.getString("role"));
                System.out.println("Address: " + rs.getString("address"));
                System.out.println("Age: " + rs.getInt("age"));
            } else {
                System.out.println("User not found.");
            }
        } catch (Exception e) {
            System.out.println("Error fetching user info: " + e.getMessage());
        }
    }
    
}
