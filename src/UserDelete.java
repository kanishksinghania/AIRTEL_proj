import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Scanner;

public class UserDelete {
    public static void deleteUser() {
        Scanner sc = new Scanner(System.in);

        System.out.print("Enter username to delete account: ");
        String username = sc.nextLine();

        try (Connection conn = Create_DB.getConnection()) {
            String checkQuery = "SELECT * FROM users WHERE username = ?";
            PreparedStatement checkStmt = conn.prepareStatement(checkQuery);
            checkStmt.setString(1, username);
            ResultSet rs = checkStmt.executeQuery();

            if (rs.next()) {
                System.out.print("Are you sure you want to delete your account? (yes/no): ");
                String confirmation = sc.nextLine();

                if ("yes".equalsIgnoreCase(confirmation)) {
                    String deleteQuery = "DELETE FROM users WHERE username = ?";
                    PreparedStatement deleteStmt = conn.prepareStatement(deleteQuery);
                    deleteStmt.setString(1, username);

                    int rowsDeleted = deleteStmt.executeUpdate();
                    if (rowsDeleted > 0) {
                        System.out.println("Account deleted successfully.");
                    } else {
                        System.out.println("Error deleting account.");
                    }
                } else {
                    System.out.println("Account deletion cancelled.");
                }
            } else {
                System.out.println("User not found with that username.");
            }

        } catch (Exception e) {
            System.out.println("Error deleting user: " + e.getMessage());
        }
    }
}
