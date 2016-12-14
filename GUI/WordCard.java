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
class WordCard {
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
  WordCard(int id, Dict dictionary) {
    this.id = id;
    dict = dictionary;
    dict.setCallback((s) -> Platform.runLater(() -> getController().definitions.setText(s)));
    checkbox = new CheckBox(getName());
    checkbox.setSelected(true);
  }

  /**
   * Getter for <code>name</code> field.
   * @return The name of this word card's dictionary.
   */
  String getName() {
    return dict.getSource();
  }

  /**
   * Getter for <code>id</code> field.
   * @return The id of this word card's dictionary as the database key.
   */
  int getId() {
    return id;
  }

  /**
   * Getter for <code>checkbox</code> field.
   * @return The checkbox reference.
   */
  CheckBox getCheckbox() {
    return checkbox;
  }

  /**
   * Check whether the dictionary should query and the card should display.
   * @return <code>True</code> if the checkbox is selected.
   */
  boolean isEnable() {
    return checkbox.isSelected();
  }

  /**
   * @return The count on how many people like the dictionary's definitions for the word.
   */
  int getCount() {
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
  Node getRoot() {
    return getController().root;
  }

  /**
   * Back-reference the upper level observable container of word card itself.
   * Mainly to simplify the re-sort after like event.
   * @param list The observable sortable word card list.
   */
  void setList(ObservableList<WordCard> list) {
    this.list = list;
  }

  /**
   * Update the count number displayed in GUI.
   * @param count The number of user like the definition.
   * @param prefer Whether this user like the definition.
   */
  void setCount(int count, boolean prefer) {
    if (prefer) {
      getController().likeButton.setText("dislike");
    }
    else {
      getController().likeButton.setText("like");
    }
    getController().likeCount.setText(Integer.toString(count));
  }

  /**
   * Query the word through network asynchronously.
   * @param word The word to query.
   */
  void query(String word) {
    dict.setWord(word);
    getController().definitions.setText("Getting definition for " + word + "...");
    new Thread(dict).start();
  }

  /**
   * Handle like event. Update the count locally (without re-query) and re-sort the sequence.
   */
  void like() {
    App.model.like(dict.getWord(), getId());
    boolean prefer = getController().likeButton.getText().equals("like");
    setCount(Integer.parseInt(getController().likeCount.getText()) + (prefer ? 1 : -1), prefer);
    list.sort((l, r) -> r.getCount() - l.getCount());
  }
}
