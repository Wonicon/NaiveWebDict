import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;

class BingWord extends Word {
  @Override
  public String getSource() {
    return "Bing";
  }

  @Override
  protected Definition parseDef(Element li) {
    Element pos_span = li.child(0);
    Element def_span = li.child(1);
    return new Definition(pos_span.text(), def_span.text());
  }

  @Override
  protected Definition[] parseDefList(Elements ul) {
    ArrayList<Definition> defs = new ArrayList<>();
    for (Element li = ul.first(); li != null; li = li.nextElementSibling()) {
      if (!li.child(0).hasClass("web")) {
        Definition def = parseDef(li);
        defs.add(def);
      }
    }
    return defs.toArray(new Definition[defs.size()]);
  }

  @Override
  protected Elements getDefList(Document doc) {
    Element element = doc.getElementsByClass("qdef").first();
    return element.child(1).children();
  }

  @Override
  protected String URL(String word) {
    return "http://cn.bing.com/dict/search?q=" + word;
  }
}
