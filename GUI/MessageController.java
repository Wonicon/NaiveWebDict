package GUI;

import Communication.WordCardMessage;
import com.sun.deploy.uitoolkit.impl.fx.ui.FXConsole;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.SingleSelectionModel;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;

public class MessageController {
  /**
   * The window of message list.
   * Reference it to avoid multiple constructions.
   */
  private Stage msgWindow = null;

  /**
   * Reference it to avoid multiple construction.
   */
  private Scene scene = null;

  /**
   * The root node of message list scene.
   * Reference it to set the scene to the stage.
   */
  @FXML
  public VBox root;

  /**
   * The list to display messages.
   */
  @FXML
  public ListView<WordCardMessage> msgListView;

  /**
   * The list to record the word card message pushed from server.
   */
  private ObservableList<WordCardMessage> wordCardMessages;

  private MainController mainController;

  public void setMainController(MainController mainController) {
    this.mainController = mainController;
  }

  /**
   * Customizing the list cell style.
   */
  @FXML
  public void initialize() {
    wordCardMessages = FXCollections.observableArrayList();
    msgListView.setItems(wordCardMessages);
    msgListView.setCellFactory(list -> new ListCell<WordCardMessage>() {
      @Override
      public void updateItem(WordCardMessage msg, boolean empty) {
        super.updateItem(msg, empty);
        setText(empty ? null : "word card shared by " + msg.getSender());
        setGraphic(null);
      }
    });
  }

  public void addAll(WordCardMessage... messages) {
    wordCardMessages.addAll(messages);
  }

  public void add(WordCardMessage message) {
    wordCardMessages.add(message);
  }

  @FXML
  public void remove(WordCardMessage msg) {
    wordCardMessages.remove(msg);
    if (wordCardMessages.size() == 0) {
      mainController.msg.setText("msg");
    }
  }

  public void show(Window prime) {
    if (msgWindow == null) {
      msgWindow = new Stage();
      msgWindow.initModality(Modality.APPLICATION_MODAL);
      msgWindow.initOwner(prime);
      if (scene == null) {
        scene = new Scene(root);
      }
      msgWindow.setScene(scene);
    }
    msgWindow.show();
  }

  public void onClick(MouseEvent mouseEvent) {
    if (mouseEvent.getClickCount() >= 2) {
      WordCardMessage msg = msgListView.getSelectionModel().getSelectedItem();
      remove(msg);
      App.model.confirm(msg.getId());
    }
  }
}
