package GUI;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class Controller {
  @FXML private TextField username;

  @FXML private PasswordField password;

  @FXML private CheckBox bing, baidu, netEase;

  @FXML private TextField word;

  @FXML private Label bingCount, baiduCount, netEaseCount;

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
    // TODO like is separate, not controlled by checkbox.
    CheckBox[] dict = { bing, baidu, netEase };
    for (int i = 0; i < dict.length; i++) {
      if (dict[i].isSelected()) {
        App.model.like(word.getText(), i + 1);
      }
    }
  }

  @FXML
  public void count() {
    // TODO Currently we can only hand coded the dictionary id in the parameter. Is there a way to make it more extensible?
    Label[] labelCount = { bingCount, baiduCount, netEaseCount };
    // We cannot directly change the UI elements in other threads. So there is a embedded callback.
    App.model.count(word.getText(), new int[] {1, 2, 3}, (int[] counts) -> Platform.runLater(() -> {
      for (int i = 0; i < counts.length; i++) {
        labelCount[i].setText(Integer.toString(counts[i]));
      }
    }));
  }
}
