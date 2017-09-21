package GUI;

import Dictionary.BingDict;
import Dictionary.Dict;
import Glossary.GlossaryCSV;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.time.*;

public class App extends Application {
  static GlossaryCSV model = new GlossaryCSV();

  static Dict dictAdapter = new BingDict();

  static Stage window;

  @Override
  public void start(Stage primaryStage) throws Exception {
    window = primaryStage;
    Scene mainScene = new Scene(FXMLLoader.load(getClass().getResource("Main.fxml")), 600, 400);

    primaryStage.setTitle("Web Dict");
    primaryStage.setScene(mainScene);

    // Stop the thread, exit completely.
    primaryStage.setOnCloseRequest(e -> {
      if (!model.isSaved()) {
        model.export("backup_" + LocalDateTime.now().toString());
      }
    });

    primaryStage.show();
  }

  public static void main(String[] args) {
    launch(args);
  }
}
