import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Logincontroller {

    @FXML
    private TextField ftusername;
    @FXML
    private PasswordField ftpassword;

    @FXML
    private UserController userController;

    @FXML
    public void navigateToSignupPage(ActionEvent event) {
        try {
            Parent signupParent = FXMLLoader.load(getClass().getResource("Signup.fxml"));
            Scene signupScene = new Scene(signupParent);
            Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
            stage.setScene(signupScene);
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void login(ActionEvent event) {
        String username = ftusername.getText();
        String password = ftpassword.getText();
        try (Connection conn = Connexion.getConn()) {
            String query = "SELECT * FROM Users WHERE username = ? AND password = ?";
            PreparedStatement statement = conn.prepareStatement(query);
            statement.setString(1, username);
            statement.setString(2, password);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                String role = resultSet.getString("role");
                Users user = new Users(resultSet.getInt("id"), resultSet.getString("username"), resultSet.getString("password"), resultSet.getString("role"));
                System.out.println("Login successful!");
                navigateToRolePage(event, role, user);
            } else {
                System.out.println("Login failed.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void navigateToRolePage(ActionEvent event, String role, Users user) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(role.equals("admin") ? "admin.fxml" : "user.fxml"));
            Parent roleParent = loader.load();
            Scene roleScene = new Scene(roleParent);

            // Get the controller instance from the loader
            if (!role.equals("admin")) {
                UserController userController = loader.getController();
                userController.setUser(user);
            }

            Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
            stage.setScene(roleScene);
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
