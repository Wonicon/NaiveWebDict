package GUI;

import Dictionary.BaiduDict;
import Dictionary.BingDict;
import Dictionary.Dict;
import Dictionary.NetEaseDict;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MainController {
  @FXML public HBox BingBox;

  @FXML public HBox BaiduBox;

  @FXML public HBox NetEaseBox;

  @FXML private CheckBox bingSel, baiduSel, netEaseSel;

  @FXML private TextField word;

  @FXML private Label bingCount, baiduCount, netEaseCount;

  @FXML private Button bingBtn, baiduBtn, netEaseBtn;

  private final String bing = "Bing", baidu = "Baidu", netEase = "NetEase";

  private Dict bingDict = new BingDict();

  private Dict baiduDict = new BaiduDict();

  private Dict netEaseDict = new NetEaseDict();

  private Map<String, Integer> dictMap = new HashMap<>();

  private Map<String, Label> labelMap = new HashMap<>();

  private Map<String, Button> btnMap = new HashMap<>();

  private Map<String, Dict> spiderMap = new HashMap<>();

  @FXML
  public void initialize() {
    System.out.println("hello");
    dictMap.put(bing, 1);
    dictMap.put(baidu, 2);
    dictMap.put(netEase, 3);

    labelMap.put(bing, bingCount);
    labelMap.put(baidu, baiduCount);
    labelMap.put(netEase, netEaseCount);

    btnMap.put(bing, bingBtn);
    btnMap.put(baidu, baiduBtn);
    btnMap.put(netEase, netEaseBtn);

    spiderMap.put(bing, bingDict);
    spiderMap.put(baidu, baiduDict);
    spiderMap.put(netEase, netEaseDict);

    bingSel.setId(bing);
    baiduSel.setId(baidu);
    netEaseSel.setId(netEase);
  }

  @FXML
  void logout() {
    App.model.logout();
    App.Window.setScene(App.Welcome);
  }

  @FXML
  public void count() {
    ArrayList<Integer> dicts = new ArrayList<>();
    ArrayList<Label> labels = new ArrayList<>();
    ArrayList<Button> btns = new ArrayList<>();
    ArrayList<Thread> tasks = new ArrayList<>();
    ArrayList<Dict> spiders = new ArrayList<>();
    for (CheckBox cb : new CheckBox[] { bingSel, baiduSel, netEaseSel }) {
      if (cb.isSelected()) {
        dicts.add(dictMap.get(cb.getId()));
        labels.add(labelMap.get(cb.getId()));
        btns.add(btnMap.get(cb.getId()));
        Dict dict = spiderMap.get(cb.getId());
        dict.setWord(word.getText());
        tasks.add(new Thread(dict));
        spiders.add(dict);
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
        if (counts[i] < 0) {
          btns.get(i).setBackground(new Background(new BackgroundFill(Color.RED, null, null)));
          labels.get(i).setText(Integer.toString(-counts[i]));
        }
        else {
          btns.get(i).setBackground(new Background(new BackgroundFill(Color.WHITE, null, null)));
          labels.get(i).setText(Integer.toString(counts[i]));
        }
      }
    }));

    tasks.forEach(Thread::start);
    try {
      for (Thread task : tasks) {
        task.join();
      }
    }
    catch (InterruptedException e) {
      e.printStackTrace();
    }
    spiders.forEach(System.out::println);
  }

  private void like(String dict) {
    App.model.like(word.getText(), dictMap.get(dict));
    count();
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
