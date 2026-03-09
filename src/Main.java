import java.util.Scanner;

public class Main {
    public static void main(String[] args) {

        LibraryService service = new LibraryService();
        Scanner sc = new Scanner(System.in);

        while (true) {
            System.out.println("\n===== Library Management System =====");
            System.out.println("1. Add Book");
            System.out.println("2. View Books");
            System.out.println("3. Issue Book");
            System.out.println("4. Return Book");
            System.out.println("5. Search Book");
            System.out.println("6. View My Issued Books");
            System.out.println("7. Exit");
            System.out.print("Enter your choice: ");

            int choice = sc.nextInt();
            sc.nextLine();  // consume leftover newline

            switch (choice) {
                case 1:
                    System.out.print("Enter Book Title: ");
                    String title = sc.nextLine();

                    System.out.print("Enter Author Name: ");
                    String author = sc.nextLine();

                    System.out.print("Enter Quantity: ");
                    int quantity = sc.nextInt();

                    service.addBook(title, author, quantity);
                    break;

                case 2:
                    service.viewBooks();
                    break;

                case 3:
                    System.out.print("Enter User ID: ");
                    int userIdIssue = sc.nextInt();

                    System.out.print("Enter Book ID: ");
                    int bookId = sc.nextInt();

                    service.issueBook(userIdIssue, bookId);
                    break;

                case 4:
                    System.out.print("Enter Issue ID: ");
                    int issueId = sc.nextInt();

                    System.out.print("Enter Book ID: ");
                    int bookIdReturn = sc.nextInt();

                    service.returnBook(issueId, bookIdReturn);
                    break;

                case 5:
                    System.out.print("Enter keyword: ");
                    String keyword = sc.nextLine();
                    service.searchBook(keyword);
                    break;

                case 6:
                    System.out.print("Enter User ID: ");
                    int userIdView = sc.nextInt();
                    service.viewIssuedBooksByUser(userIdView);
                    break;

                case 7:
                    System.out.println("Exiting...");
                    System.exit(0);

                default:
                    System.out.println("Invalid choice!");
            }
        }
    }
}