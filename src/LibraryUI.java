import javax.swing.*;
import java.awt.*;
import java.util.Vector;

public class LibraryUI extends JFrame {

    private LibraryService service = new LibraryService();

    private JTable mainTable;
    private JFrame tableFrame;

    public LibraryUI() {
        setTitle("📚 Library Management System");

        ImageIcon icon = new ImageIcon("book.png");
        setIconImage(icon.getImage());

        setSize(700, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        // Background Color
        getContentPane().setBackground(new Color(30, 39, 46));
        setLayout(new BorderLayout());

        // Title Label

        // Button Panel
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(new Color(24,32,44));

        ImageIcon logo = new ImageIcon("book.png");
        Image img = logo.getImage().getScaledInstance(60,60,Image.SCALE_SMOOTH);
        JLabel logoLabel = new JLabel(new ImageIcon(img));
        logoLabel.setHorizontalAlignment(JLabel.CENTER);

        JLabel title = new JLabel("📚 Library Management System", JLabel.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 26));
        title.setForeground(Color.WHITE);

        topPanel.add(logoLabel, BorderLayout.NORTH);
        topPanel.add(title, BorderLayout.CENTER);

        add(topPanel, BorderLayout.NORTH);

        JPanel panel = new JPanel();
        panel.setBackground(new Color(30, 39, 46));
        panel.setLayout(new GridLayout(3, 2, 20, 20));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 60, 40, 60));

        JButton addBookBtn = createButton("Add Book");
        addBookBtn.addActionListener(e -> showAddBookForm());
        JButton viewBookBtn = createButton("View Books");
        viewBookBtn.addActionListener(e -> showBooksTable());
        JButton issueBookBtn = createButton("Issue Book");
        issueBookBtn.addActionListener(e -> showIssueBookForm());
        JButton returnBookBtn = createButton("Return Book");
        returnBookBtn.addActionListener(e -> showReturnBookForm());
        JButton searchBtn = createButton("Search Book");
        searchBtn.addActionListener(e -> showSearchForm());
        JButton issuedBtn = createButton("My Issued Books");
        issuedBtn.addActionListener(e -> showIssuedBooksForm());

        panel.add(addBookBtn);
        panel.add(viewBookBtn);
        panel.add(issueBookBtn);
        panel.add(returnBookBtn);
        panel.add(searchBtn);
        panel.add(issuedBtn);

        add(panel, BorderLayout.CENTER);

        setVisible(true);
    }

    private JButton createButton(String text) {
        JButton button = new JButton(text);
        button.setFocusPainted(false);
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setBackground(new Color(52, 152, 219));
        button.setForeground(Color.WHITE);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return button;
    }

    private void showBooksTable() {

        Vector<String> columns = new Vector<>();
        columns.add("ID");
        columns.add("Title");
        columns.add("Author");
        columns.add("Quantity");

        Vector<Vector<Object>> data = service.getAllBooks();

        mainTable = new JTable(data, columns);
        mainTable.setRowHeight(28);
        mainTable.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        mainTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        mainTable.setEnabled(false);

        JScrollPane scrollPane = new JScrollPane(mainTable);

        tableFrame = new JFrame("All Books");
        tableFrame.setSize(700, 400);
        tableFrame.setLocationRelativeTo(null);
        tableFrame.add(scrollPane);
        tableFrame.setVisible(true);
    }

    private void showAddBookForm() {

        JFrame formFrame = new JFrame("Add New Book");
        formFrame.setSize(400, 300);
        formFrame.setLocationRelativeTo(null);
        formFrame.setLayout(new GridLayout(4, 2, 10, 10));

        JLabel titleLabel = new JLabel("Title:");
        JTextField titleField = new JTextField();

        JLabel authorLabel = new JLabel("Author:");
        JTextField authorField = new JTextField();

        JLabel quantityLabel = new JLabel("Quantity:");
        JTextField quantityField = new JTextField();

        JButton submitBtn = new JButton("Add Book");

        submitBtn.setBackground(new Color(46, 204, 113));
        submitBtn.setForeground(Color.WHITE);

        formFrame.add(titleLabel);
        formFrame.add(titleField);
        formFrame.add(authorLabel);
        formFrame.add(authorField);
        formFrame.add(quantityLabel);
        formFrame.add(quantityField);
        formFrame.add(new JLabel());
        formFrame.add(submitBtn);

        submitBtn.addActionListener(e -> {
            String title = titleField.getText().trim();
            String author = authorField.getText().trim();
            String qtyText = quantityField.getText().trim();

            if (title.isEmpty() || author.isEmpty() || qtyText.isEmpty()) {
                JOptionPane.showMessageDialog(formFrame, "All fields are required!");
                return;
            }

            int quantity;

            try {
                quantity = Integer.parseInt(qtyText);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(formFrame, "Quantity must be a number!");
                return;
            }

            service.addBook(title, author, quantity);

            refreshTable();

            JOptionPane.showMessageDialog(formFrame, "Book Added Successfully!");
            formFrame.dispose();
        });

        formFrame.setVisible(true);
    }

    private void showIssueBookForm() {

        JFrame issueFrame = new JFrame("Issue Book");
        issueFrame.setSize(400, 250);
        issueFrame.setLocationRelativeTo(null);
        issueFrame.setLayout(new GridLayout(3, 2, 10, 10));

        JLabel userLabel = new JLabel("User ID:");
        JTextField userField = new JTextField();

        JLabel bookLabel = new JLabel("Book ID:");
        JTextField bookField = new JTextField();

        JButton issueBtn = new JButton("Issue Book");

        issueBtn.setBackground(new Color(241, 196, 15));
        issueBtn.setForeground(Color.BLACK);

        issueFrame.add(userLabel);
        issueFrame.add(userField);
        issueFrame.add(bookLabel);
        issueFrame.add(bookField);
        issueFrame.add(new JLabel());
        issueFrame.add(issueBtn);

        issueBtn.addActionListener(e -> {
            try {
                int userId = Integer.parseInt(userField.getText());
                int bookId = Integer.parseInt(bookField.getText());

                service.issueBook(userId, bookId);

                refreshTable();

                JOptionPane.showMessageDialog(issueFrame, "Operation Completed!");
                issueFrame.dispose();

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(issueFrame, "Invalid Input!");
            }
        });

        issueFrame.setVisible(true);
    }

    private void showReturnBookForm() {

        JFrame returnFrame = new JFrame("Return Book");
        returnFrame.setSize(400, 250);
        returnFrame.setLocationRelativeTo(null);
        returnFrame.setLayout(new GridLayout(3, 2, 10, 10));

        JLabel issueLabel = new JLabel("Issue ID:");
        JTextField issueField = new JTextField();

        JLabel bookLabel = new JLabel("Book ID:");
        JTextField bookField = new JTextField();

        JButton returnBtn = new JButton("Return Book");

        returnBtn.setBackground(new Color(231, 76, 60));
        returnBtn.setForeground(Color.WHITE);

        returnFrame.add(issueLabel);
        returnFrame.add(issueField);
        returnFrame.add(bookLabel);
        returnFrame.add(bookField);
        returnFrame.add(new JLabel());
        returnFrame.add(returnBtn);

        returnBtn.addActionListener(e -> {
            try {
                int issueId = Integer.parseInt(issueField.getText());
                int bookId = Integer.parseInt(bookField.getText());

                int confirm = JOptionPane.showConfirmDialog(
                        returnFrame,
                        "Are you sure you want to return this book?",
                        "Confirm Return",
                        JOptionPane.YES_NO_OPTION
                );

                if (confirm == JOptionPane.YES_OPTION) {
                    service.returnBook(issueId, bookId);
                    refreshTable();
                }

                refreshTable();

                JOptionPane.showMessageDialog(returnFrame, "Return Process Completed!");
                returnFrame.dispose();

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(returnFrame, "Invalid Input!");
            }
        });

        returnFrame.setVisible(true);
    }

    private void showSearchForm() {

        JFrame searchFrame = new JFrame("Search Book");
        searchFrame.setSize(400, 200);
        searchFrame.setLocationRelativeTo(null);
        searchFrame.setLayout(new GridLayout(2, 2, 10, 10));

        JLabel searchLabel = new JLabel("Enter Keyword:");
        JTextField searchField = new JTextField();

        JButton searchButton = new JButton("Search");
        searchButton.setBackground(new Color(155, 89, 182));
        searchButton.setForeground(Color.WHITE);

        searchFrame.add(searchLabel);
        searchFrame.add(searchField);
        searchFrame.add(new JLabel());
        searchFrame.add(searchButton);

        searchButton.addActionListener(e -> {
            String keyword = searchField.getText();
            showSearchResults(keyword);
            searchFrame.dispose();
        });

        searchFrame.setVisible(true);
    }

    private void showSearchResults(String keyword) {

        Vector<String> columns = new Vector<>();
        columns.add("ID");
        columns.add("Title");
        columns.add("Author");
        columns.add("Quantity");

        Vector<Vector<Object>> data = service.searchBooksForTable(keyword);

        JTable table = new JTable(data, columns);
        table.setRowHeight(28);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));

        JScrollPane scrollPane = new JScrollPane(table);

        JFrame resultFrame = new JFrame("Search Results");
        resultFrame.setSize(600, 350);
        resultFrame.setLocationRelativeTo(null);
        resultFrame.add(scrollPane);
        resultFrame.setVisible(true);
    }

    private void refreshTable() {
        if (mainTable != null) {

            Vector<String> columns = new Vector<>();
            columns.add("ID");
            columns.add("Title");
            columns.add("Author");
            columns.add("Quantity");

            Vector<Vector<Object>> newData = service.getAllBooks();

            mainTable.setModel(new javax.swing.table.DefaultTableModel(newData, columns));
        }
    }

    private void showIssuedBooksForm() {

        JFrame frame = new JFrame("My Issued Books");
        frame.setSize(400, 200);
        frame.setLocationRelativeTo(null);
        frame.setLayout(new GridLayout(2, 2, 10, 10));

        JLabel userLabel = new JLabel("Enter User ID:");
        JTextField userField = new JTextField();

        JButton viewBtn = new JButton("View");
        viewBtn.setBackground(new Color(52, 152, 219));
        viewBtn.setForeground(Color.WHITE);

        frame.add(userLabel);
        frame.add(userField);
        frame.add(new JLabel());
        frame.add(viewBtn);

        viewBtn.addActionListener(e -> {
            try {
                int userId = Integer.parseInt(userField.getText());
                showIssuedBooksTable(userId);
                frame.dispose();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(frame, "Invalid Input!");
            }
        });

        frame.setVisible(true);
    }

    private void showIssuedBooksTable(int userId) {

        Vector<String> columns = new Vector<>();
        columns.add("Title");
        columns.add("Issue Date");

        Vector<Vector<Object>> data = service.getIssuedBooksForTable(userId);

        JTable table = new JTable(data, columns);
        table.setRowHeight(28);

        JScrollPane scrollPane = new JScrollPane(table);

        JFrame resultFrame = new JFrame("Issued Books");
        resultFrame.setSize(500, 300);
        resultFrame.setLocationRelativeTo(null);
        resultFrame.add(scrollPane);
        resultFrame.setVisible(true);
    }

    public static void main(String[] args) {
        new LibraryUI();
    }
}