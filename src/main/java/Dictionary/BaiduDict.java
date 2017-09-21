package Dictionary;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class BaiduDict extends Dict {
  @Override
  public String getSource() {
    return "Baidu";
  }

  @Override
  protected String URL(String word) {
    return "http://www.iciba.com/" + word;
  }

  @Override
  protected Elements getDefList(Document doc) {
    return doc.getElementsByClass("in-base").first().children().get(1).children();
  }

  @Override
  protected Definition parseDef(Element li) {
    return new Definition(li.child(0).text(), li.child(1).text().split("；"));
  }
}
