package GUI;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

/**
 * This class controls the interaction with the word root to display the single query result.
 */
public class WordCardController {
  @FXML
  public Label dictName;

  @FXML
  public Label likeCount;

  @FXML
  public Button likeButton;

  @FXML
  public Label definitions;

  @FXML
  public VBox root;

  private WordCard model;

  public void setModel(WordCard card) {
    model = card;
    dictName.setText(model.getName());
  }

  @FXML
  public void like() {
    model.like();
  }
}
