package GUI;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class WelcomeController {
  @FXML private TextField username;

  @FXML private PasswordField password;

  @FXML
  void register() {
    App.model.register(username.getText(), password.getText(), success -> Platform.runLater(() -> {
      Alert alert = new Alert(Alert.AlertType.INFORMATION);
      alert.setTitle("Register");
      alert.setHeaderText(null);
      if (success) {
        alert.setContentText("register succeeded");
      }
      else {
        alert.setContentText("register failed");
      }
      alert.showAndWait();
    }));
  }

  @FXML
  void login() {
    App.model.login(username.getText(), password.getText(), auth -> Platform.runLater(() -> {
      if (auth) {
        App.window.setScene(App.mainScene);
        App.window.setTitle(username.getText());
      }
      else {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Login");
        alert.setHeaderText(null);
        alert.setContentText("login failed");
        alert.showAndWait();
      }
    }));
  }

}
