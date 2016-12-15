package GUI;

import Communication.Client;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class App extends Application {
  static Client model = new Client();

  static Stage window;

  static Scene welcome;

  static Scene mainScene;

  @Override
  public void start(Stage primaryStage) throws Exception {
    window = primaryStage;
    welcome = new Scene(FXMLLoader.load(getClass().getResource("Welcome.fxml")), 300, 275);
    mainScene = new Scene(FXMLLoader.load(getClass().getResource("Main.fxml")), 600, 400);

    primaryStage.setTitle("Web Dict");
    primaryStage.setScene(welcome);

    // Stop the thread, exit completely.
    primaryStage.setOnCloseRequest(e -> model.stop());

    primaryStage.show();
  }


  public static void main(String[] args) {
    launch(args);
  }
}
