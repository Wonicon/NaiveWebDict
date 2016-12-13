package GUI;

import javafx.scene.control.ListCell;

public class WordCardCell extends ListCell<WordCardController> {
  @Override
  public void updateItem(WordCardController wordCard, boolean empty) {
    System.out.println("hello");
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
