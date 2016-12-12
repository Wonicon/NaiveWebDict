package GUI;

import Communication.Client;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class App extends Application {
  static Client model = new Client();

  static Stage Window;

  static Scene Welcome;

  static Scene Main;

  @Override
  public void start(Stage primaryStage) throws Exception {
    Welcome = new Scene(FXMLLoader.load(getClass().getResource("Welcome.fxml")), 300, 275);
    Main = new Scene(FXMLLoader.load(getClass().getResource("Main.fxml")), 300, 275);
    Window = primaryStage;

    primaryStage.setTitle("Web Dict");
    primaryStage.setScene(Welcome);

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
