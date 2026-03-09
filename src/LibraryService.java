import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Date;
import java.time.LocalDate;
import java.util.Vector;

public class LibraryService {

    // ADD BOOK METHOD
    public void addBook(String title, String author, int quantity) {
        try {
            Connection con = DBConnection.getConnection();

            String query = "INSERT INTO books (title, author, quantity) VALUES (?, ?, ?)";
            PreparedStatement ps = con.prepareStatement(query);

            ps.setString(1, title);
            ps.setString(2, author);
            ps.setInt(3, quantity);

            ps.executeUpdate();

            System.out.println("Book added successfully!");

            con.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // VIEW BOOKS METHOD
    public void viewBooks() {
        try {
            Connection con = DBConnection.getConnection();

            String query = "SELECT * FROM books";
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery(query);

            System.out.println("\n---- Available Books ----");

            while (rs.next()) {
                System.out.println(
                        "ID: " + rs.getInt("id") +
                                ", Title: " + rs.getString("title") +
                                ", Author: " + rs.getString("author") +
                                ", Quantity: " + rs.getInt("quantity")
                );
            }

            con.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void issueBook(int userId, int bookId) {
        try {
            Connection con = DBConnection.getConnection();

            String duplicateCheck = "SELECT * FROM issued_books WHERE user_id = ? AND book_id = ? AND return_date IS NULL";
            PreparedStatement dupStmt = con.prepareStatement(duplicateCheck);
            dupStmt.setInt(1, userId);
            dupStmt.setInt(2, bookId);

            ResultSet dupRs = dupStmt.executeQuery();

            if (dupRs.next()) {
                System.out.println("User already has this book issued!");
                con.close();
                return;
            }
            // Step 1: Check book quantity
            String checkQuery = "SELECT quantity FROM books WHERE id = ?";
            PreparedStatement checkStmt = con.prepareStatement(checkQuery);
            checkStmt.setInt(1, bookId);

            ResultSet rs = checkStmt.executeQuery();

            if (rs.next()) {
                int quantity = rs.getInt("quantity");

                if (quantity > 0) {

                    // Step 2: Insert into issued_books
                    String issueQuery = "INSERT INTO issued_books (user_id, book_id, issue_date) VALUES (?, ?, ?)";
                    PreparedStatement issueStmt = con.prepareStatement(issueQuery);

                    issueStmt.setInt(1, userId);
                    issueStmt.setInt(2, bookId);
                    issueStmt.setDate(3, Date.valueOf(LocalDate.now()));

                    issueStmt.executeUpdate();

                    // Step 3: Reduce quantity
                    String updateQuery = "UPDATE books SET quantity = quantity - 1 WHERE id = ?";
                    PreparedStatement updateStmt = con.prepareStatement(updateQuery);
                    updateStmt.setInt(1, bookId);
                    updateStmt.executeUpdate();

                    System.out.println("Book issued successfully!");

                } else {
                    System.out.println("Book out of stock!");
                }
            } else {
                System.out.println("Book not found!");
            }

            con.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void returnBook(int issueId, int bookId) {
        try {
            Connection con = DBConnection.getConnection();

            String getIssue = "SELECT issue_date FROM issued_books WHERE id = ?";
            PreparedStatement getStmt = con.prepareStatement(getIssue);
            getStmt.setInt(1, issueId);
            ResultSet issueRs = getStmt.executeQuery();

            if (issueRs.next()) {
                java.sql.Date issueDate = issueRs.getDate("issue_date");

                long days = java.time.temporal.ChronoUnit.DAYS.between(
                        issueDate.toLocalDate(),
                        java.time.LocalDate.now()
                );

                if (days > 7) {
                    long fine = (days - 7) * 5;
                    System.out.println("Late return! Fine: ₹" + fine);
                }
            }
            // Step 1: Update return_date
            String returnQuery = "UPDATE issued_books SET return_date = ? WHERE id = ?";
            PreparedStatement returnStmt = con.prepareStatement(returnQuery);
            returnStmt.setDate(1, java.sql.Date.valueOf(java.time.LocalDate.now()));
            returnStmt.setInt(2, issueId);

            int rows = returnStmt.executeUpdate();

            if (rows > 0) {

                // Step 2: Increase quantity
                String updateBook = "UPDATE books SET quantity = quantity + 1 WHERE id = ?";
                PreparedStatement bookStmt = con.prepareStatement(updateBook);
                bookStmt.setInt(1, bookId);
                bookStmt.executeUpdate();

                System.out.println("Book returned successfully!");

            } else {
                System.out.println("Invalid issue ID!");
            }

            con.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void searchBook(String keyword) {
        try {
            Connection con = DBConnection.getConnection();

            String query = "SELECT * FROM books WHERE title LIKE ? OR author LIKE ?";
            PreparedStatement ps = con.prepareStatement(query);

            ps.setString(1, "%" + keyword + "%");
            ps.setString(2, "%" + keyword + "%");

            ResultSet rs = ps.executeQuery();

            System.out.println("\n--- Search Results ---");

            while (rs.next()) {
                System.out.println(
                        "ID: " + rs.getInt("id") +
                                ", Title: " + rs.getString("title") +
                                ", Author: " + rs.getString("author") +
                                ", Quantity: " + rs.getInt("quantity")
                );
            }

            con.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void viewIssuedBooksByUser(int userId) {
        try {
            Connection con = DBConnection.getConnection();

            String query = "SELECT b.title, i.issue_date " +
                    "FROM issued_books i " +
                    "JOIN books b ON i.book_id = b.id " +
                    "WHERE i.user_id = ? AND i.return_date IS NULL";

            PreparedStatement ps = con.prepareStatement(query);
            ps.setInt(1, userId);

            ResultSet rs = ps.executeQuery();

            System.out.println("\n--- Books Issued By User ---");

            while (rs.next()) {
                System.out.println(
                        "Title: " + rs.getString("title") +
                                ", Issued On: " + rs.getDate("issue_date")
                );
            }

            con.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public Vector<Vector<Object>> getAllBooks() {
        Vector<Vector<Object>> data = new Vector<>();

        try {
            Connection con = DBConnection.getConnection();
            String query = "SELECT * FROM books";
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery(query);

            while (rs.next()) {
                Vector<Object> row = new Vector<>();
                row.add(rs.getInt("id"));
                row.add(rs.getString("title"));
                row.add(rs.getString("author"));
                row.add(rs.getInt("quantity"));
                data.add(row);
            }

            con.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return data;
    }
    public Vector<Vector<Object>> searchBooksForTable(String keyword) {

        Vector<Vector<Object>> data = new Vector<>();

        try {
            Connection con = DBConnection.getConnection();

            String query = "SELECT * FROM books WHERE title LIKE ? OR author LIKE ?";
            PreparedStatement ps = con.prepareStatement(query);
            ps.setString(1, "%" + keyword + "%");
            ps.setString(2, "%" + keyword + "%");

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Vector<Object> row = new Vector<>();
                row.add(rs.getInt("id"));
                row.add(rs.getString("title"));
                row.add(rs.getString("author"));
                row.add(rs.getInt("quantity"));
                data.add(row);
            }

            con.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return data;
    }

    public Vector<Vector<Object>> getIssuedBooksForTable(int userId) {

        Vector<Vector<Object>> data = new Vector<>();

        try {
            Connection con = DBConnection.getConnection();

            String query = "SELECT b.title, i.issue_date " +
                    "FROM issued_books i " +
                    "JOIN books b ON i.book_id = b.id " +
                    "WHERE i.user_id = ? AND i.return_date IS NULL";

            PreparedStatement ps = con.prepareStatement(query);
            ps.setInt(1, userId);

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Vector<Object> row = new Vector<>();
                row.add(rs.getString("title"));
                row.add(rs.getDate("issue_date"));
                data.add(row);
            }

            con.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return data;
    }
}