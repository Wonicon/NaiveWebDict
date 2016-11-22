import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

class BingWord extends Word {
  @Override
  public String getSource() {
    return "Bing";
  }

  @Override
  protected Elements getDefList(Document doc) {
    Element element = doc.getElementsByClass("qdef").first();
    return element.child(1).children();
  }

  @Override
  protected Definition parseDef(Element li) {
    Element pos_span = li.child(0);
    Element def_span = li.child(1);
    return new Definition(pos_span.text(), def_span.text());
  }

  @Override
  protected String URL(String word) {
    return "http://cn.bing.com/dict/search?q=" + word;
  }
}
