package GUI;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * This class handle the selection of online users and interaction with them.
 */
public class UserSelection {
  @FXML
  public VBox root;

  @FXML
  public ListView<String> userListView;

  @FXML
  public Button send;

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
      App.model.send(items.get(0), "hello world");
    }
    else {
      System.out.println("Select a user");
    }

    ((Stage)root.getScene().getWindow()).close();
  }
}
