package GUI;

import javafx.scene.control.ListCell;

public class WordCardCell extends ListCell<WordCard> {
  @Override
  public void updateItem(WordCard wordCard, boolean empty) {
    super.updateItem(wordCard, empty);
    if (empty) {
      setText(null);
      setGraphic(null);
    }
    else {
      setText(null);
      setGraphic(wordCard.getRoot());
    }
  }
}
