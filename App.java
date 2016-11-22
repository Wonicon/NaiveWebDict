import java.io.IOException;
import java.util.Scanner;

public class App {
  public static void main(String[] args) throws IOException {
    Scanner input = new Scanner(System.in);
    System.out.print("word: ");
    long time = System.nanoTime();
    Word word = new BingWord().query(input.next());
    Word netEaseWord = new NetEaseWord().query(word.getWord());
    Word baiduWord = new BaiduWord().query(word.getWord());
    System.out.println(word);
    System.out.println(netEaseWord);
    System.out.println(baiduWord);
    System.out.println((System.nanoTime() - time) / 1000_000_000.0);
  }
}
