package GUI;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Controller {
  @FXML private TextField username;

  @FXML private PasswordField password;

  @FXML private CheckBox bingSel, baiduSel, netEaseSel;

  @FXML private TextField word;

  @FXML private Label bingCount, baiduCount, netEaseCount;

  private Map<String, Integer> dictMap = new HashMap<>();

  private Map<String, Label> labelMap = new HashMap<>();

  private final String bing = "Bing", baidu = "Baidu", netEase = "NetEase";

  @FXML
  public void initialize() {
    System.out.println("hello");
    dictMap.put(bing, 1);
    dictMap.put(baidu, 2);
    dictMap.put(netEase, 3);

    labelMap.put(bing, bingCount);
    labelMap.put(baidu, baiduCount);
    labelMap.put(netEase, netEaseCount);

    bingSel.setId(bing);
    baiduSel.setId(baidu);
    netEaseSel.setId(netEase);
  }

  @FXML
  void register() {
    App.model.register(username.getText(), password.getText());
  }

  @FXML
  void login() {
    App.model.login(username.getText(), password.getText());
  }

  @FXML
  void logout() {
    App.model.logout();
  }

  @FXML
  public void count() {
    ArrayList<Integer> dicts = new ArrayList<>();
    ArrayList<Label> labels = new ArrayList<>();
    for (CheckBox cb : new CheckBox[] { bingSel, baiduSel, netEaseSel }) {
      if (cb.isSelected()) {
        dicts.add(dictMap.get(cb.getId()));
        labels.add(labelMap.get(cb.getId()));
      }
    }

    // Fool...
    int[] dictsArray = new int[dicts.size()];
    for (int i = 0; i < dictsArray.length; i++) {
      dictsArray[i] = dicts.get(i);
    }

    // We cannot directly change the UI elements in other threads. So there is a embedded callback.
    App.model.count(word.getText(), dictsArray, (int[] counts) -> Platform.runLater(() -> {
      for (int i = 0; i < counts.length; i++) {
        labels.get(i).setText(Integer.toString(counts[i]));
      }
    }));
  }

  private void like(String dict) {
    App.model.like(word.getText(), dictMap.get(dict));
  }

  @FXML
  public void likeBing() {
    like(bing);
  }

  @FXML
  public void likeBaidu() {
    like(baidu);
  }

  @FXML
  public void likeNetEase() {
    like(netEase);
  }
}
