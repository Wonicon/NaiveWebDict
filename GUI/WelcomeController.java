package GUI;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class WelcomeController {
  @FXML private TextField username;

  @FXML private PasswordField password;

  @FXML
  void register() {
    App.model.register(username.getText(), password.getText());
  }

  @FXML
  void login() {
    App.model.login(username.getText(), password.getText(), (boolean auth) -> Platform.runLater(() -> {
      if (auth) {
        App.Window.setScene(App.Main);
      }
    }));
  }

}
