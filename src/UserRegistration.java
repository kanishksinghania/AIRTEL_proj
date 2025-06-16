import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.Scanner;
import java.util.regex.Pattern;

public class UserRegistration {

    private static final String SIMPLE_EMAIL_REGEX = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";
    private static final Pattern SIMPLE_EMAIL_PATTERN = Pattern.compile(SIMPLE_EMAIL_REGEX);

    private static boolean isValidEmail(String email) {
        return SIMPLE_EMAIL_PATTERN.matcher(email).matches();
    }

    private static boolean isValidPhone(String phone) {
        return phone.matches("\\d{10}");
    }

    public static void registerUser() {
        Scanner scanner = new Scanner(System.in);

        System.out.print("Enter first name: ");
        String fname = scanner.nextLine();
        
        System.out.print("Enter last name: ");
        String lname = scanner.nextLine();

        String email;
        while (true) {
            System.out.print("Enter email: ");
            email = scanner.nextLine();
            if (isValidEmail(email)) {
                break;
            } else {
                System.out.println("Invalid email format. Please enter a valid email (e.g., user@example.com).");
            }
        }

        System.out.print("Enter password: ");
        String password = scanner.nextLine();

        System.out.print("Enter your age: ");
        int age = scanner.nextInt();
        scanner.nextLine(); 

        String phone;
        while (true) {
            System.out.print("Enter phone (10 digits): ");
            phone = scanner.nextLine();
            if (isValidPhone(phone)) {
                break;
            } else {
                System.out.println("Invalid phone number. Please enter a valid 10-digit phone number.");
            }
        }

        String role;
        while (true) {
            System.out.print("Enter role (admin/user): ");
            role = scanner.nextLine().toLowerCase();
            if (role.equals("admin") || role.equals("user")) {
                break;
            } else {
                System.out.println("Invalid role. Please enter 'admin' or 'user'.");
            }
        }

        System.out.print("Enter Address (HNo City State Country): ");
        String address = scanner.nextLine();

        try (Connection conn = Create_DB.getConnection()) {
            String query = "INSERT INTO users (fname, lname, email, password, age, phone, role, address) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, fname);
            stmt.setString(2, lname);
            stmt.setString(3, email);
            stmt.setString(4, password);
            stmt.setInt(5, age);
            stmt.setString(6, phone);
            stmt.setString(7, role);
            stmt.setString(8, address);

            int rowsInserted = stmt.executeUpdate();
            if (rowsInserted > 0) {
                System.out.println("User registered successfully!");
            }
        } catch (Exception e) {
            System.out.println("Error registering user: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        registerUser();
    }
}
