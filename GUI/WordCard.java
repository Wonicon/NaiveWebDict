package GUI;

import Dictionary.Dict;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.CheckBox;

import java.io.IOException;

/**
 * This class describes necessary properties for a word root shown in the list view
 * combined to a specific online dictionary.
 * Act as a model for WordCardController.
 */
public class WordCard {
  /**
   * <code>id</code> is used to query the server database.
   */
  private int id;

  /**
   * Online dictionary.
   */
  private Dict dict;

  /**
   * This check box is used in the Main scene
   * to control the visibility and functionality
   * of this word root.
   */
  private CheckBox checkbox;

  /**
   * The controller.
   */
  private WordCardController controller;

  /**
   * The list from the parent node.
   */
  private ObservableList<WordCard> list;

  /**
   * @param id The primary key for the server database query.
   *           It is bad to hard-coded it, though.
   *           It mainly provides information about preference and count.
   * @param dictionary The concrete dictionary to perform spider works.
   *                   It provides the name and definitions for words.
   */
  public WordCard(int id, Dict dictionary) {
    this.id = id;
    dict = dictionary;
    dict.setCallback((s) -> Platform.runLater(() -> getController().definitions.setText(s)));
    checkbox = new CheckBox(getName());
    checkbox.setSelected(true);
  }

  public String getName() {
    return dict.getSource();
  }

  public int getId() {
    return id;
  }

  public CheckBox getCheckbox() {
    return checkbox;
  }

  public boolean isEnable() {
    return checkbox.isSelected();
  }

  /**
   * @return The count on how many people like the dictionary's definitions for the word.
   */
  public int getCount() {
    return Integer.parseInt(getController().likeCount.getText());
  }

  /**
   * FXML lazy loader.
   * @return The correctly initialized controller with fxml elements.
   */
  private WordCardController getController() {
    if (controller == null) {
      FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("WordCard.fxml"));
      try {
        fxmlLoader.load();
      }
      catch (IOException e) {
        e.printStackTrace();
      }
      controller = fxmlLoader.getController();
      controller.setModel(this);
    }
    return controller;
  }

  /**
   * Tell list view how to display this object.
   * @return The root container of the view.
   */
  public Node getRoot() {
    return getController().root;
  }

  public void setList(ObservableList<WordCard> l) {
    list = l;
  }

  public void setCount(int count, boolean prefer) {
    if (prefer) {
      getController().likeButton.setText("dislike");
    }
    else {
      getController().likeButton.setText("like");
    }
    getController().likeCount.setText(Integer.toString(count));
  }

  public void query(String word) {
    dict.setWord(word);
    getController().definitions.setText("Getting definition for " + word + "...");
    new Thread(dict).start();
  }

  public void like() {
    App.model.like(dict.getWord(), getId());
    boolean prefer = getController().likeButton.getText().equals("like");
    setCount(Integer.parseInt(getController().likeCount.getText()) + (prefer ? 1 : -1), prefer);
    list.sort((l, r) -> r.getCount() - l.getCount());
  }
}
