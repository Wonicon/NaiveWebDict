package Dictionary;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;

public abstract class Dict implements Runnable {
  private Definition[] definitions;

  private String word;

  private long time;

  public Definition getDefAt(int i) {
    return definitions[i];
  }

  public void setWord(String s) {
    if (word == null || !s.equals(word)) {
      word = s;
      definitions = null;
    }
  }

  public String getWord() {
    return word;
  }

  public double getTime() {
    return time / 1000_000_000.0;
  }

  private Definition[] parseDefList(Elements ul) {
    ArrayList<Definition> defList = new ArrayList<>();
    for (Element li = ul.first(); li != null; li = li.nextElementSibling()) {
      Definition def = parseDef(li);
      defList.add(def);
    }
    return defList.toArray(new Definition[defList.size()]);
  }


  Dict query() {
    time = System.nanoTime();
    assert (this.definitions == null);
    try {
      Document doc = Jsoup.connect(URL(word)).get();
      Elements defList = getDefList(doc);
      this.definitions = parseDefList(defList);
    }
    catch (IOException e) {
      System.out.println(e.toString());
    }
    catch (NullPointerException e) {
      System.err.println(word + " nof found from " + getSource());
    }
    time = System.nanoTime() - time;
    return this;
  }

  public Dict() {
    definitions = null;
    word = null;
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder(word + " (" + getSource() + ", " + getTime() + "s)\n");
    if (definitions != null) {
      for (Definition def : definitions) {
        sb.append(def.getPos()).append(" ").append(def.getDef()).append("\n");
      }
    }
    else {
      sb.append("N/A\n");
    }
    return sb.toString();
  }

  @Override
  public void run() {
    query();
  }

  public abstract String getSource();

  protected abstract String URL(String word);

  protected abstract Elements getDefList(Document doc);

  protected abstract Definition parseDef(Element li);
}
