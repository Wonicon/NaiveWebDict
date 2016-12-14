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
import javafx.scene.layout.HBox;
import javafx.stage.*;
import javafx.event.ActionEvent;

import java.io.IOException;

public class MainController {
  @FXML
  public HBox dictSel;

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

  @FXML
  public void initialize() {
    observeWordCards = FXCollections.observableArrayList();
    wordCardList.setItems(observeWordCards);
    wordCardList.setCellFactory((ListView<WordCard> list) -> new WordCardCell());
    for (WordCard card : wordCards) {
      dictSel.getChildren().add(card.getCheckbox());
      card.setList(observeWordCards);
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
    App.model.list(users -> Platform.runLater(() -> {
      Stage popup = new Stage();
      FXMLLoader fxml = new FXMLLoader(getClass().getResource("UserSelection.fxml"));
      try {
        fxml.load();
      }
      catch (IOException e) {
        e.printStackTrace();
      }
      ((UserSelection)fxml.getController()).setUserList(users);
      popup.setScene(new Scene(fxml.getRoot()));
      popup.initOwner(((Node)ev.getTarget()).getScene().getWindow());
      popup.initModality(Modality.APPLICATION_MODAL);
      popup.show();
    }));
  }
}
