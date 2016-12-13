package GUI;

import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

/**
 * This class controls the interaction with the word card to display the single query result.
 */
public class WordCardController {
  @FXML
  public Label dictName;

  @FXML
  public Label likeCount;

  @FXML
  public Button likeBtn;

  @FXML
  public Label def;

  @FXML
  public VBox card;

  private String name;

  private int id;

  public String getName() {
    return this.name;
  }

  public int getId() {
    return this.id;
  }

  public void setName(String name, int id) {
    this.name = name;
    this.id = id;
    this.dictName.setText(name);
  }

  public Node getRoot() {
    return this.card;
  }
}
