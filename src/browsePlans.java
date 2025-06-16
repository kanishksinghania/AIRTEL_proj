import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Scanner;

public class browsePlans {
    public static void main(String[] args) {  
        plans();
    }

    public static void plans() {
        Scanner scanner = new Scanner(System.in);

        System.out.println("What type of plans would you like to see?");
        System.out.println("1) Only Data Plans");
        System.out.println("2) Truly Unlimited Plans");
        System.out.print("Choose an option: ");
        
        int choice = scanner.nextInt();
        scanner.nextLine();  

        switch (choice) {
            case 1:
                printAllDataPlans();
                break;
            case 2:
                printAllUnlimitedPlans();
                break;
            default:
                System.out.println("Invalid option, please try again.");
                return; 
        }

        System.out.println("Do you want help choosing the best plan? (yes/no):");
        String confirmation = scanner.nextLine();

        if ("yes".equalsIgnoreCase(confirmation)) {
            System.out.print("Enter your data requirement (in GB): ");
            int data_need = scanner.nextInt();
            System.out.print("Enter the plan duration you want (in days): ");
            int days_need = scanner.nextInt();
            scanner.nextLine();  

            if (choice == 1) {
                findBestDataPlan(data_need, days_need);
            } else {
                findBestUnlimitedPlan(data_need, days_need);
            }
        } else {
            System.out.println("Thanks for exploring the plans!");
        }
    }

    private static void printAllDataPlans() {
        try (Connection conn = Create_DB.getConnection()) {
            String query = "SELECT * FROM only_data";
            PreparedStatement stmt = conn.prepareStatement(query);
            ResultSet rs = stmt.executeQuery();

            System.out.println("Available Data Plans:");
            while (rs.next()) {
                System.out.println("Plan ID: " + rs.getInt("plan_id"));
                System.out.println("Price: " + rs.getInt("price"));
                System.out.println("Data: " + rs.getInt("data_gb") + " GB");
                System.out.println("Validity: " + rs.getInt("validity_days") + " days");
                System.out.println("------------------------");
            }
        } catch (Exception e) {
            System.out.println("Error fetching data plans: " + e.getMessage());
        }
    }

    private static void printAllUnlimitedPlans() {
        try (Connection conn = Create_DB.getConnection()) {
            String query = "SELECT * FROM unlimited_tt";
            PreparedStatement stmt = conn.prepareStatement(query);
            ResultSet rs = stmt.executeQuery();

            System.out.println("Available Truly Unlimited Plans:");
            while (rs.next()) {
                System.out.println("Plan ID: " + rs.getInt("TU_id"));
                System.out.println("Price: " + rs.getInt("tu_price"));
                System.out.println("Data per Day: " + rs.getInt("data_per_day") + " GB");
                System.out.println("Validity: " + rs.getInt("validity_days") + " days");
                System.out.println("------------------------");
            }
        } catch (Exception e) {
            System.out.println("Error fetching unlimited plans: " + e.getMessage());
        }
    }

    private static void findBestDataPlan(int data_need, int days_need) {
        try (Connection conn = Create_DB.getConnection()) {
            String query = "SELECT * FROM only_data";
            PreparedStatement stmt = conn.prepareStatement(query);
            ResultSet rs = stmt.executeQuery();

            int closestMatchId = -1;
            int minDifference = Integer.MAX_VALUE;

            while (rs.next()) {
                int data_gb = rs.getInt("data_gb");
                int validity_days = rs.getInt("validity_days");
                
                int dataDifference = Math.abs(data_gb - data_need);
                int daysDifference = Math.abs(validity_days - days_need);
                int totalDifference = dataDifference + daysDifference;

                if (totalDifference < minDifference) {
                    minDifference = totalDifference;
                    closestMatchId = rs.getInt("plan_id");
                }
            }

            if (closestMatchId != -1) {
                System.out.println("\n✅ Best Matched Data Plan:");
                printPlanDetails("only_data", closestMatchId);
            } else {
                System.out.println("No suitable data plan found.");
            }
        } catch (Exception e) {
            System.out.println("Error finding the best data plan: " + e.getMessage());
        }
    }

    private static void findBestUnlimitedPlan(int data_need, int days_need) {
        try (Connection conn = Create_DB.getConnection()) {
            String query = "SELECT * FROM unlimited_tt";
            PreparedStatement stmt = conn.prepareStatement(query);
            ResultSet rs = stmt.executeQuery();

            int closestMatchId = -1;
            int minDifference = Integer.MAX_VALUE;

            while (rs.next()) {
                int data_per_day = rs.getInt("data_per_day");
                int validity_days = rs.getInt("validity_days");
                
                int dataDifference = Math.abs(data_per_day - data_need);
                int daysDifference = Math.abs(validity_days - days_need);
                int totalDifference = dataDifference + daysDifference;

                if (totalDifference < minDifference) {
                    minDifference = totalDifference;
                    closestMatchId = rs.getInt("TU_id");
                }
            }

            if (closestMatchId != -1) {
                System.out.println("\n✅ Best Matched Unlimited Plan:");
                printPlanDetails("unlimited_tt", closestMatchId);
            } else {
                System.out.println("No suitable unlimited plan found.");
            }
        } catch (Exception e) {
            System.out.println("Error finding the best unlimited plan: " + e.getMessage());
        }
    }

    private static void printPlanDetails(String tableName, int planId) {
        try (Connection conn = Create_DB.getConnection()) {
            String query = "SELECT * FROM " + tableName + " WHERE " + 
                           (tableName.equals("only_data") ? "plan_id" : "TU_id") + " = ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setInt(1, planId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                System.out.println("Plan ID: " + rs.getInt(tableName.equals("only_data") ? "plan_id" : "TU_id"));
                System.out.println("Price: " + (tableName.equals("only_data") ? rs.getInt("price") : rs.getInt("tu_price")));
                System.out.println("Data: " + (tableName.equals("only_data") ? rs.getInt("data_gb") + " GB" : rs.getInt("data_per_day") + " GB/day"));
                System.out.println("Validity: " + rs.getInt("validity_days") + " days");
            } else {
                System.out.println("No plan found with ID: " + planId);
            }
        } catch (Exception e) {
            System.out.println("Error fetching plan details: " + e.getMessage());
        }
    }
}
