package GUI;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * This class handle the selection of online users and interaction with them.
 */
public class UserSelectionController {
  @FXML
  public VBox root;

  @FXML
  public ListView<String> userListView;

  @FXML
  public Button send;

  @FXML
  public Button refresh;

  @FXML
  public CheckBox includeOffline;

  /**
   * The message content to send.
   */
  private String content;

  /**
   * Set the message content.
   * @param content The content generated from wordCardList.
   */
  public void setContent(String content) {
    this.content = content;
  }

  /**
   * Set user list to display.
   * @param users Users to display.
   */
  public void setUserList(String[] users) {
    ObservableList<String> observableUserList = FXCollections.observableArrayList(users);
    userListView.setItems(observableUserList);
  }

  @FXML
  public void initialize() {
    userListView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
  }

  @FXML
  public void send(ActionEvent actionEvent) {
    ObservableList<String> items = userListView.getSelectionModel().getSelectedItems();
    if (items.size() > 0) {
      App.model.send(items.get(0), content);
    }
    else {
      System.out.println("Select a user");
    }

    ((Stage)root.getScene().getWindow()).close();
  }

  public void refresh(ActionEvent actionEvent) {
    App.model.list(includeOffline.isSelected(), users -> Platform.runLater(() -> setUserList(users)));
  }
}
