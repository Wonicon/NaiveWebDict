import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;

abstract class Word {
  private Definition[] definitions;

  private String word;

  public Definition getDefAt(int i) {
    return definitions[i];
  }

  public String getWord() {
    return word;
  }

  private Definition[] parseDefList(Elements ul) {
    ArrayList<Definition> defList = new ArrayList<>();
    for (Element li = ul.first(); li != null; li = li.nextElementSibling()) {
      Definition def = parseDef(li);
      defList.add(def);
    }
    return defList.toArray(new Definition[defList.size()]);
  }


  Word set(String word) {
    this.word = word;
    try {
      Document doc = Jsoup.connect(URL(word)).get();
      Elements defList = getDefList(doc);
      this.definitions = parseDefList(defList);
    } catch (IOException e) {
      System.out.println(e.toString());
    }
    return this;
  }

  Word() {
    definitions = null;
    word = null;
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder(word + "\n");
    for (Definition def : definitions) {
      sb.append(def.getPos()).append(" ").append(def.getDef()).append("\n");
    }
    return sb.toString();
  }

  public abstract String getSource();

  protected abstract Definition parseDef(Element li);

  protected abstract Elements getDefList(Document doc);

  protected abstract String URL(String word);
}
