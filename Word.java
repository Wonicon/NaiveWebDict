import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;

abstract class Word {
  private Definition[] definitions;

  private String word;

  private long time;

  public Definition getDefAt(int i) {
    return definitions[i];
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


  Word query(String word) {
    time = System.nanoTime();
    this.word = word;
    try {
      Document doc = Jsoup.connect(URL(word)).get();
      Elements defList = getDefList(doc);
      this.definitions = parseDefList(defList);
    } catch (IOException e) {
      System.out.println(e.toString());
    }
    time = System.nanoTime() - time;
    return this;
  }

  Word() {
    definitions = null;
    word = null;
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder(word + " (" + getSource() + ", " + getTime() + "s)\n");
    for (Definition def : definitions) {
      sb.append(def.getPos()).append(" ").append(def.getDef()).append("\n");
    }
    return sb.toString();
  }

  public abstract String getSource();

  protected abstract String URL(String word);

  protected abstract Elements getDefList(Document doc);

  protected abstract Definition parseDef(Element li);
}
