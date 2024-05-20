import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class AdminController {
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
    private TextField titleField;
    @FXML
    private TextField authorField;

    private ObservableList<Books> bookData = FXCollections.observableArrayList();

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
        try (Connection conn = Connexion.getConn()) {
            String query = "SELECT * FROM Books";
            PreparedStatement statement = conn.prepareStatement(query);
            ResultSet resultSet = statement.executeQuery();
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
    public void addBook(ActionEvent event) {
        String title = titleField.getText();
        String author = authorField.getText();
        try (Connection conn = Connexion.getConn()) {
            String query = "INSERT INTO Books (title, author, available) VALUES (?, ?, 1)";
            PreparedStatement statement = conn.prepareStatement(query, PreparedStatement.RETURN_GENERATED_KEYS);
            statement.setString(1, title);
            statement.setString(2, author);
            int rowsInserted = statement.executeUpdate();
            if (rowsInserted > 0) {
                System.out.println("Book added successfully!");
                ResultSet generatedKeys = statement.getGeneratedKeys();
                if (generatedKeys.next()) {
                    int newBookId = generatedKeys.getInt(1);
                    Books newBook = new Books(newBookId, title, author, true, 0);
                    bookData.add(newBook);
                    titleField.clear();
                    authorField.clear();
                } else {
                    System.out.println("Failed to retrieve generated keys for the new book.");
                }
            } else {
                System.out.println("Failed to add the book.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void modifyBook(ActionEvent event) {
        Books selectedBook = booksTable.getSelectionModel().getSelectedItem();
        if (selectedBook != null) {
            String newTitle = titleField.getText();
            String newAuthor = authorField.getText();
            try (Connection conn = Connexion.getConn()) {
                String query = "UPDATE Books SET title = ?, author = ? WHERE id = ?";
                PreparedStatement statement = conn.prepareStatement(query);
                statement.setString(1, newTitle);
                statement.setString(2, newAuthor);
                statement.setInt(3, selectedBook.getId());
                int rowsUpdated = statement.executeUpdate();
                if (rowsUpdated > 0) {
                    bookData.remove(selectedBook);
                    Books updatedBook = new Books(selectedBook.getId(), newTitle, newAuthor, selectedBook.isAvailable(), selectedBook.getBorrowedby());
                    bookData.add(updatedBook);
                    titleField.clear();
                    authorField.clear();
                    System.out.println("Book modified successfully!");
                } else {
                    System.out.println("Failed to modify the book.");
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    @FXML
    public void deleteBook(ActionEvent event) {
        Books selectedBook = booksTable.getSelectionModel().getSelectedItem();
        if (selectedBook != null) {
            try (Connection conn = Connexion.getConn()) {
                String query = "DELETE FROM Books WHERE id = ?";
                PreparedStatement statement = conn.prepareStatement(query);
                statement.setInt(1, selectedBook.getId());
                int rowsDeleted = statement.executeUpdate();
                if (rowsDeleted > 0) {
                    bookData.remove(selectedBook);
                    System.out.println("Book deleted successfully!");
                } else {
                    System.out.println("Failed to delete the book.");
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
