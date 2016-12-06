package GUI;

import Communication.Client;

import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class Controller {
  @FXML private TextField username;

  @FXML private PasswordField password;

  @FXML
  void register() {
    App.model.register(username.getText(), password.getText());
  }

  @FXML
  void login() {
    App.model.login(username.getText(), password.getText());
  }

  @FXML
  void logout() {
    App.model.logout();
  }
}
