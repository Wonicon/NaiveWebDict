package GUI;

import Dictionary.Definition;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;

public class MainController {
  @FXML
  public VBox defList;

  @FXML
  public TextField source;

  @FXML
  public TextField sentence;

  @FXML
  private TextField word;

  private ToggleGroup group = new ToggleGroup();

  private Stage popup = new Stage();

  @FXML
  public void initialize() {
    popup.initModality(Modality.NONE);
    popup.setTitle("Words Currently Added");
    try {
      VBox vBox = FXMLLoader.load(getClass().getResource("CSV.fxml"));
      Scene scene = new Scene(vBox);
      popup.setScene(scene);
    }
    catch (IOException e) {
      Alert alert = new Alert(Alert.AlertType.ERROR, e.toString());
      alert.show();
    }
  }

  @FXML
  public void query() {
    // Check input
    String input = word.getText();
    for (char ch : input.toCharArray()) {
      if (!Character.isLetter(ch)) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Query");
        alert.setHeaderText(null);
        alert.setContentText("The input should be an english word");
        alert.showAndWait();
        word.clear();
        return;
      }
    }

    if (input.isEmpty()) {
      Alert alert = new Alert(Alert.AlertType.WARNING);
      alert.setTitle("Query");
      alert.setHeaderText(null);
      alert.setContentText("The input should not be empty");
      alert.showAndWait();
      return;
    }

    App.dictAdapter.setWord(word.getText());
    App.dictAdapter.run();
    group.getToggles().clear();
    for (Definition def: App.dictAdapter.getDefinitions()) {
      HBox hBox = new HBox();
      hBox.setAlignment(Pos.BASELINE_LEFT);
      hBox.setSpacing(10);
      hBox.getChildren().add(new Label(def.getPos()));
      for (String detailedDef: def.getDefs()) {
        ToggleButton tb = new ToggleButton(detailedDef);
        hBox.getChildren().add(tb);
        group.getToggles().add(tb);
      }
      defList.getChildren().add(hBox);
    }
  }

  @FXML
  public void add() {
    ToggleButton tb = (ToggleButton)group.getSelectedToggle();
    if (tb == null) {
      Alert alert = new Alert(Alert.AlertType.WARNING, "Must select a definition!");
      alert.show();
      return;
    }

    String selectedDef = tb.getText();
    // Find out pos
    String pos = null;
    for (Definition def: App.dictAdapter.getDefinitions()) {
      for (String detailedDef: def.getDefs()) {
        if (detailedDef.equals(selectedDef)) {
          pos = def.getPos();
          break;
        }
      }
    }
    App.model.addEntry(App.dictAdapter.getWord(), pos, selectedDef, source.getText(), sentence.getText());

    word.clear();
    defList.getChildren().clear();
    sentence.clear();
  }

  @FXML
  public void view() {
    Label label = (Label) popup.getScene().getRoot().lookup("#content");
    label.setText(App.model.view());
    popup.show();
  }

  @FXML
  public void export() {
    FileChooser chooser = new FileChooser();
    chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV File", "*.csv"));
    File file = chooser.showSaveDialog(App.window);
    if (file != null) {
      App.model.export(file.getName());
    }
  }
}
