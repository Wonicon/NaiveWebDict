package GUI;

import Communication.Client;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class App extends Application {
  static Client model = new Client();

  @FXML
  private String username;

  @Override
  public void start(Stage primaryStage) throws Exception {
    Parent root = FXMLLoader.load(getClass().getResource("GUI.fxml"));
    primaryStage.setTitle("Hello World");
    primaryStage.setScene(new Scene(root, 300, 275));

    // Stop the thread, exit completely.
    primaryStage.setOnCloseRequest(e -> {
      model.stop();
      System.out.println("Stop the model");
    });

    primaryStage.show();
  }


  public static void main(String[] args) {
    launch(args);
  }
}