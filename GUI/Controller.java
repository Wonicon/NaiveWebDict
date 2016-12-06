package GUI;

import Communication.Client;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class Controller {
  @FXML private TextField username;

  @FXML private PasswordField password;

  @FXML private CheckBox bing;

  @FXML private CheckBox baidu;

  @FXML private CheckBox netEase;

  @FXML private TextField word;

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

  @FXML
  public void like() {
    CheckBox[] dict = { bing, baidu, netEase };
    for (int i = 0; i < dict.length; i++) {
      if (dict[i].isSelected()) {
        App.model.like(word.getText(), i + 1);
      }
    }
  }
}
