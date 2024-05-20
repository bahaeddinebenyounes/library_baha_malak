import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import javafx.event.ActionEvent;
import javafx.scene.control.TextField;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserController {
    @FXML
    private TableView<Books> booksTable;
    @FXML
    private TableColumn<Books, Integer> idColumn;
    @FXML
    private TableColumn<Books, String> titleColumn;
    @FXML
    private TableColumn<Books, String> authorColumn;
    @FXML
    private TableColumn<Books, Boolean> availableColumn;
    @FXML
    private TableColumn<Books, Integer> borrowedbyColumn;

    @FXML
    private TextField returnBookIdField;

    private ObservableList<Books> bookData = FXCollections.observableArrayList();
    private Users loggedInUser;

    @FXML
    public void initialize() {
        loadBooksFromDatabase();
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        titleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
        authorColumn.setCellValueFactory(new PropertyValueFactory<>("author"));
        availableColumn.setCellValueFactory(new PropertyValueFactory<>("available"));
        borrowedbyColumn.setCellValueFactory(new PropertyValueFactory<>("borrowedby"));
        booksTable.setItems(bookData);
    }

    public void setUser(Users user) {
        this.loggedInUser = user;
    }

    @FXML
    public void logout(ActionEvent event) {
        // Code to handle logout
        // For example, navigate back to the login page
        navigateToLoginPage(event);
    }

    private void navigateToLoginPage(ActionEvent event) {
        try {
            Parent loginParent = FXMLLoader.load(getClass().getResource("Login.fxml"));
            Scene loginScene = new Scene(loginParent);
            Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
            stage.setScene(loginScene);
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadBooksFromDatabase() {
        String query = "SELECT * FROM Books";
        try (Connection conn = Connexion.getConn();
                PreparedStatement statement = conn.prepareStatement(query);
                ResultSet resultSet = statement.executeQuery()) {

            bookData.clear();
            while (resultSet.next()) {
                Books book = new Books(
                        resultSet.getInt("id"),
                        resultSet.getString("title"),
                        resultSet.getString("author"),
                        resultSet.getBoolean("available"),
                        resultSet.getInt("borrowedby"));
                bookData.add(book);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void borrowBook(ActionEvent event) {
        Books selectedBook = booksTable.getSelectionModel().getSelectedItem();
        if (selectedBook != null && loggedInUser != null) {
            int userId = loggedInUser.getId(); // Get the logged-in user's ID
            int bookId = selectedBook.getId();
            String checkAvailabilityQuery = "SELECT available FROM Books WHERE id = ?";
            String borrowQuery = "UPDATE Books SET available = 0, borrowedby = ? WHERE id = ?";

            try (Connection conn = Connexion.getConn();
                    PreparedStatement availabilityStatement = conn.prepareStatement(checkAvailabilityQuery);
                    PreparedStatement borrowStatement = conn.prepareStatement(borrowQuery)) {

                availabilityStatement.setInt(1, bookId);
                try (ResultSet availabilityResult = availabilityStatement.executeQuery()) {
                    if (availabilityResult.next() && availabilityResult.getBoolean("available")) {
                        borrowStatement.setInt(1, userId);
                        borrowStatement.setInt(2, bookId);
                        int rowsUpdated = borrowStatement.executeUpdate();
                        if (rowsUpdated > 0) {
                            System.out.println("Book borrowed successfully!");
                            loadBooksFromDatabase(); // Refresh the table view
                        } else {
                            System.out.println("Failed to borrow the book.");
                        }
                    } else {
                        System.out.println("Book is not available for borrowing.");
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } else {
            if (selectedBook == null) {
                System.out.println("Please select a book to borrow.");
            } else {
                System.out.println("No user is logged in.");
            }
        }
    }

    @FXML
    public void returnBook(ActionEvent event) {
        Books selectedBook = booksTable.getSelectionModel().getSelectedItem();
        if (selectedBook != null && !selectedBook.isAvailable()) {
            int bookId = selectedBook.getId();
            int userId = loggedInUser.getId(); // Get the logged-in user's ID
            int borrowedBy = selectedBook.getBorrowedby(); // Get the ID of the user who borrowed the book

            if (userId == borrowedBy) { // Check if the logged-in user is the one who borrowed the book
                String returnQuery = "UPDATE Books SET available = 1, borrowedby = NULL WHERE id = ?";

                try (Connection conn = Connexion.getConn();
                        PreparedStatement returnStatement = conn.prepareStatement(returnQuery)) {
                    returnStatement.setInt(1, bookId);
                    int rowsUpdated = returnStatement.executeUpdate();
                    if (rowsUpdated > 0) {
                        System.out.println("Book returned successfully!");
                        loadBooksFromDatabase(); // Refresh the table view
                    } else {
                        System.out.println("Failed to return the book.");
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            } else {
                System.out.println("You can only return the book you have borrowed.");
            }
        } else {
            if (selectedBook == null) {
                System.out.println("Please select a book to return.");
            } else {
                System.out.println("Selected book is already available.");
            }
        }
    }
}