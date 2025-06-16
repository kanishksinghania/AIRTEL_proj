import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Scanner;

public class UserUpdate {
    public static void updateUser() {
        Scanner sc = new Scanner(System.in);

        System.out.print("Enter email to update details: ");
        String email = sc.nextLine();

        try (Connection conn = Create_DB.getConnection()) {
            String checkQuery = "SELECT * FROM users WHERE email = ?";
            PreparedStatement checkStmt = conn.prepareStatement(checkQuery);
            checkStmt.setString(1, email);
            ResultSet rs = checkStmt.executeQuery();

            if (rs.next()) {
                System.out.println("User found. What would you like to update?");
                System.out.println("1. Update first name");
                System.out.println("2. Update last name");
                System.out.println("3. Update phone number");
                System.out.println("4. Update password");
                System.out.println("5. Update age");
                System.out.println("6. Update role (admin/user)");
                System.out.println("7. Update address (HNo City State Country)");
                System.out.print("Choose an option: ");
                int choice = sc.nextInt();
                sc.nextLine(); 

                String updateQuery;
                PreparedStatement updateStmt;

                switch (choice) {
                    case 1 -> {
                        System.out.print("Enter new first name: ");
                        String newFirstName = sc.nextLine();
                        updateQuery = "UPDATE users SET fname = ? WHERE email = ?";
                        updateStmt = conn.prepareStatement(updateQuery);
                        updateStmt.setString(1, newFirstName);
                        updateStmt.setString(2, email);
                    }
                    case 2 -> {
                        System.out.print("Enter new last name: ");
                        String newLastName = sc.nextLine();
                        updateQuery = "UPDATE users SET lname = ? WHERE email = ?";
                        updateStmt = conn.prepareStatement(updateQuery);
                        updateStmt.setString(1, newLastName);
                        updateStmt.setString(2, email);
                    }
                    case 3 -> {
                        System.out.print("Enter new phone number: ");
                        String newPhone = sc.nextLine();
                        updateQuery = "UPDATE users SET phone = ? WHERE email = ?";
                        updateStmt = conn.prepareStatement(updateQuery);
                        updateStmt.setString(1, newPhone);
                        updateStmt.setString(2, email);
                    }
                    case 4 -> {
                        System.out.print("Enter new password: ");
                        String newPassword = sc.nextLine();
                        updateQuery = "UPDATE users SET password = ? WHERE email = ?";
                        updateStmt = conn.prepareStatement(updateQuery);
                        updateStmt.setString(1, newPassword);
                        updateStmt.setString(2, email);
                    }
                    case 5 -> {
                        System.out.print("Enter new age: ");
                        int newAge = sc.nextInt();
                        updateQuery = "UPDATE users SET age = ? WHERE email = ?";
                        updateStmt = conn.prepareStatement(updateQuery);
                        updateStmt.setInt(1, newAge);
                        updateStmt.setString(2, email);
                    }
                    case 6 -> {
                        System.out.print("Enter new role (admin/user): ");
                        String newRole = sc.nextLine();
                        if (!newRole.equalsIgnoreCase("admin") && !newRole.equalsIgnoreCase("user")) {
                            System.out.println("Invalid role. Please enter 'admin' or 'user'.");
                            return;
                        }
                        updateQuery = "UPDATE users SET role = ? WHERE email = ?";
                        updateStmt = conn.prepareStatement(updateQuery);
                        updateStmt.setString(1, newRole);
                        updateStmt.setString(2, email);
                    }
                    case 7 -> {
                        System.out.print("Enter new address (HNo City State Country): ");
                        String newAddress = sc.nextLine();
                        updateQuery = "UPDATE users SET address = ? WHERE email = ?";
                        updateStmt = conn.prepareStatement(updateQuery);
                        updateStmt.setString(1, newAddress);
                        updateStmt.setString(2, email);
                    }
                    default -> {
                        System.out.println("Invalid option.");
                        return;
                    }
                }

                int rowsUpdated = updateStmt.executeUpdate();
                if (rowsUpdated > 0) {
                    System.out.println("User information updated successfully!");
                } else {
                    System.out.println("No changes made.");
                }
            } else {
                System.out.println("User not found with that email.");
            }

        } catch (Exception e) {
            System.out.println("Error updating user: " + e.getMessage());
        }
    }
}
