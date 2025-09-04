package Library;

import java.sql.*;
import java.util.Scanner;

public class LibraryManagement {
    static final String URL = "jdbc:mysql://localhost:3306/librarydb";
    static final String USER = "root";   // your MySQL username
    static final String PASS = "Sree321";   // your MySQL password

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        try (Connection con = DriverManager.getConnection(URL, USER, PASS)) {
            System.out.println(" Connected to Database!");

            while (true) {
                System.out.println("---- Library Menu ----");
                System.out.println("1. Add Book");
                System.out.println("2. View Books");
                System.out.println("3. Issue Book");
                System.out.println("4. Return Book");
                System.out.println("5. Exit");
                System.out.print("Choose: ");
                int choice = sc.nextInt();
                sc.nextLine();

                switch (choice) {
                    case 1 -> addBook(con, sc);
                    case 2 -> viewBooks(con);
                    case 3 -> issueBook(con, sc);
                    case 4 -> returnBook(con, sc);
                    case 5 -> {
                        System.out.println("Exiting...");
                        return;
                    }
                    default -> System.out.println("Invalid choice!");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Add Book
    static void addBook(Connection con, Scanner sc) throws SQLException {
        System.out.print("Enter Book Title: ");
        String title = sc.nextLine();
        System.out.print("Enter Author: ");
        String author = sc.nextLine();

        String sql = "INSERT INTO books(title, author, available) VALUES(?, ?, TRUE)";
        try (PreparedStatement pst = con.prepareStatement(sql)) {
            pst.setString(1, title);
            pst.setString(2, author);
            pst.executeUpdate();
            System.out.println(" Book Added!");
        }
    }

    // View Books
    static void viewBooks(Connection con) throws SQLException {
        String sql = "SELECT * FROM books";
        try (Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            System.out.println("ID | Title | Author | Available");
            System.out.println("---------------------------------");
            while (rs.next()) {
                System.out.printf("%d | %s | %s | %s%n",
                        rs.getInt("id"),
                        rs.getString("title"),
                        rs.getString("author"),
                        rs.getBoolean("available") ? "Yes" : "No");
            }
        }
    }

    // Issue Book
    static void issueBook(Connection con, Scanner sc) throws SQLException {
        System.out.print("Enter Book ID to Issue: ");
        int id = sc.nextInt();

        String checkSql = "SELECT available FROM books WHERE id = ?";
        try (PreparedStatement checkPst = con.prepareStatement(checkSql)) {
            checkPst.setInt(1, id);
            try (ResultSet rs = checkPst.executeQuery()) {
                if (rs.next()) {
                    if (rs.getBoolean("available")) {
                        String updateSql = "UPDATE books SET available = FALSE WHERE id = ?";
                        try (PreparedStatement pst = con.prepareStatement(updateSql)) {
                            pst.setInt(1, id);
                            pst.executeUpdate();
                            System.out.println("Book Issued!");
                        }
                    } else {
                        System.out.println(" Book is already issued!");
                    }
                } else {
                    System.out.println(" Book ID not found!");
                }
            }
        }
    }

    // Return Book
    static void returnBook(Connection con, Scanner sc) throws SQLException {
        System.out.print("Enter Book ID to Return: ");
        int id = sc.nextInt();

        String checkSql = "SELECT available FROM books WHERE id = ?";
        try (PreparedStatement checkPst = con.prepareStatement(checkSql)) {
            checkPst.setInt(1, id);
            try (ResultSet rs = checkPst.executeQuery()) {
                if (rs.next()) {
                    if (!rs.getBoolean("available")) {
                        String updateSql = "UPDATE books SET available = TRUE WHERE id = ?";
                        try (PreparedStatement pst = con.prepareStatement(updateSql)) {
                            pst.setInt(1, id);
                            pst.executeUpdate();
                            System.out.println(" Book Returned!");
                        }
                    } else {
                        System.out.println("Book was not issued!");
                    }
                } else {
                    System.out.println(" Book ID not found!");
                }
            }
        }
    }
}