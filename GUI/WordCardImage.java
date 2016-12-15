package GUI;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.Scene;
import javafx.scene.SnapshotParameters;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;

class WordCardImage {
  /**
   * The area to render the content.
   */
  private VBox vbox;

  /**
   * The window to display content.
   */
  private Stage stage;

  WordCardImage(String content) {
    Label label = new Label(content);
    label.setWrapText(true);
    vbox = new VBox(label);
    Button btn = new Button("save");
    btn.setOnAction(e -> save());
    Scene scene = new Scene(new VBox(vbox, btn));
    stage = new Stage();
    stage.setScene(scene);
  }

  void save() {
    WritableImage image = vbox.snapshot(new SnapshotParameters(), null);

    File file = new FileChooser().showSaveDialog(stage);
    try {
      // Oh, old fashioned swing api.
      ImageIO.write(SwingFXUtils.fromFXImage(image, null), "png", file);
    }
    catch (IOException e) {
      System.out.println(e.toString());
    }
  }

  void display() {
    stage.show();
  }
}
