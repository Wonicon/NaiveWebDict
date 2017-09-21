package Dictionary;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class BingDict extends Dict {
  @Override
  public String getSource() {
    return "Bing";
  }

  @Override
  protected String URL(String word) {
    return "http://cn.bing.com/dict/search?q=" + word;
  }

  @Override
  protected Elements getDefList(Document doc) {
    return doc.getElementsByClass("qdef")
              .first().child(1).children();
  }

  @Override
  protected Definition parseDef(Element li) {
    return new Definition(li.child(0).text(), li.child(1).text());
  }
}
