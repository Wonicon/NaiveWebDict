import java.io.IOException;
import java.util.Scanner;

public class App {
  public static void main(String[] args) throws IOException {
    Scanner input = new Scanner(System.in);
    System.out.print("word: ");
    Word word = new BingWord().set(input.next());
    Word netEaseWord = new NetEaseWord().set(word.getWord());
    Word baiduWord = new BaiduWord().set(word.getWord());
    System.out.println(word);
    System.out.println(netEaseWord);
    System.out.println(baiduWord);
  }
}
