import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class Signupcontroller {

    @FXML
    private TextField ftusername;
    @FXML
    private PasswordField ftpassword;

    @FXML
    private Button loginButton;

    @FXML
    public void signup(ActionEvent event) {
        String username = ftusername.getText();
        String password = ftpassword.getText();
        try (Connection conn = Connexion.getConn()) {
            String query = "INSERT INTO Users (username, password, role) VALUES (?, ?, 'member')";
            PreparedStatement statement = conn.prepareStatement(query);
            statement.setString(1, username);
            statement.setString(2, password);
            int rowsInserted = statement.executeUpdate();
            if (rowsInserted > 0) {
                System.out.println("User signed up successfully!");
                navigateToLoginPage(event);
            } else {
                System.out.println("Failed to sign up user.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void navigateToLoginPage(ActionEvent event) {
        try {
            Parent loginParent = FXMLLoader.load(getClass().getResource("login.fxml"));
            Scene loginScene = new Scene(loginParent);
            Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
            stage.setScene(loginScene);
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}