import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class NetEaseDict extends Dict {
  @Override
  public String getSource() {
    return "NetEase";
  }

  @Override
  protected String URL(String word) {
    return "http://dict.youdao.com/w/" + word;
  }

  @Override
  protected Elements getDefList(Document doc) {
    return doc.getElementById("phrsListTab")
              .getElementsByClass("trans-container")
              .first().child(0).children();
  }

  @Override
  protected Definition parseDef(Element li) {
    String[] s = li.text().split(" ", 2);
    return new Definition(s[0], s[1]);
  }
}
