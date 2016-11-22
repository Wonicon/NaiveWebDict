import com.sun.org.apache.xerces.internal.dom.DeferredNotationImpl;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class BaiduWord extends Word {
  @Override
  public String getSource() {
    return "Baidu";
  }

  @Override
  protected String URL(String word) {
    return "http://dict.baidu.com/s?wd=" + word;
  }

  @Override
  protected Elements getDefList(Document doc) {
    return doc.getElementById("simple_means-wrapper")
              .getElementsByClass("en-content")
              .first().child(0).children();
  }

  @Override
  protected Definition parseDef(Element p) {
    return new Definition(p.child(0).text(), p.child(1).text());
  }
}
