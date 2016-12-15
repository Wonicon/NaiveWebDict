package GUI;

import Dictionary.BaiduDict;
import Dictionary.BingDict;
import Dictionary.NetEaseDict;

import javafx.fxml.FXML;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.stage.*;
import javafx.event.ActionEvent;

import java.io.IOException;

public class MainController {
  @FXML
  public HBox dictSel;

  @FXML
  public Button msg;

  @FXML
  private TextField word;

  private WordCard[] wordCards = {
      new WordCard(1, new BingDict()),
      new WordCard(2, new BaiduDict()),
      new WordCard(3, new NetEaseDict()),
  };

  private ObservableList<WordCard> observeWordCards;

  @FXML
  private ListView<WordCard> wordCardList;

  private MessageController msgController;

  /**
   * Add dynamic elements to the list view and other containers.
   */
  @FXML
  public void initialize() {
    observeWordCards = FXCollections.observableArrayList();
    wordCardList.setItems(observeWordCards);
    // Allow multiple selection, then user can select multiple card to send.
    wordCardList.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

    // Render the cell as the WordCard.fxml describes.
    wordCardList.setCellFactory(list -> new ListCell<WordCard>(){
      @Override
      public void updateItem(WordCard wordCard, boolean empty) {
        super.updateItem(wordCard, empty);
        setGraphic(empty ? null : wordCard.getRoot());
        setText(null);
      }
    });

    // Add checkboxes representing the dictionaries.
    for (WordCard card : wordCards) {
      dictSel.getChildren().add(card.getCheckbox());
      card.setList(observeWordCards);
    }

    try {
      FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("Message.fxml"));
      fxmlLoader.load();
      msgController = fxmlLoader.getController();
      msgController.setMainController(this);
      App.model.setNotifySendHandler(w -> Platform.runLater(() -> {
        msgController.add(w);
        msg.setText("msg (new)");
      }));
    }
    catch (IOException e) {
      System.out.println(e.toString());
    }
  }

  @FXML
  void logout() {
    App.model.logout();
    App.Window.setScene(App.Welcome);
  }

  private boolean allowAll() {
    boolean r = true;
    for (WordCard wordCard : wordCards) {
      r = r && !wordCard.isEnable();
    }
    return r;
  }

  @FXML
  public void count() {
    wordCardList.setVisible(true);
    observeWordCards.clear();
    if (allowAll()) {
      observeWordCards.addAll(wordCards);
    }
    else {
      for (WordCard wordCard : wordCards) {
        if (wordCard.isEnable()) {
          observeWordCards.add(wordCard);
        }
      }
    }

    for (WordCard wordCard : wordCards) {
      wordCard.query(word.getText());
    }

    App.model.count(word.getText(), observeWordCards.stream().mapToInt(WordCard::getId).toArray(),
        counts -> Platform.runLater(() -> {
          for (int i = 0; i < counts.length; i++) {
            observeWordCards.get(i).setCount(counts[i] < 0 ? -counts[i] : counts[i], counts[i] < 0);
          }
          observeWordCards.sort((left, right) -> right.getCount() - left.getCount());
        })
    );
  }

  @FXML
  public void list(ActionEvent ev) {
  }

  @FXML
  public void showMsg(ActionEvent actionEvent) {
    msgController.show(((Node)actionEvent.getTarget()).getScene().getWindow());
  }

  @FXML
  public void onMouseClicked(MouseEvent mouseEvent) {
    if (mouseEvent.getButton() == MouseButton.SECONDARY) {
      StringBuilder builder = new StringBuilder();
      wordCardList.getSelectionModel().getSelectedItems().forEach(builder::append);

      App.model.list(false, users -> Platform.runLater(() -> {
        Stage popup = new Stage();
        FXMLLoader fxml = new FXMLLoader(getClass().getResource("UserSelection.fxml"));
        try {
          fxml.load();
        }
        catch (IOException e) {
          e.printStackTrace();
        }
        ((UserSelectionController)fxml.getController()).setUserList(users);
        ((UserSelectionController)fxml.getController()).setContent(builder.toString());
        popup.setScene(new Scene(fxml.getRoot()));
        popup.initOwner(((Node)mouseEvent.getTarget()).getScene().getWindow());
        popup.initModality(Modality.APPLICATION_MODAL);
        popup.show();
      }));
    }
  }
}
