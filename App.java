import java.util.Scanner;

public class App {
  public static void main(String[] args) throws Exception {
    Scanner input = new Scanner(System.in);
    Dict[] dicts = new Dict[]{new BingDict(), new NetEaseDict(), new BaiduDict()};

    while (input.hasNext()) {
      System.out.print("word: ");
      String word = input.next();
      long time = System.nanoTime();
      Thread[] threads = new Thread[dicts.length];

      for (Dict dict : dicts)
        dict.setWord(word);
      for (int i = 0; i < threads.length; i++)
        threads[i] = new Thread(dicts[i]);
      for (Thread thread : threads)
        thread.start();
      for (Thread thread : threads)
        thread.join();
      for (Dict dict : dicts)
        System.out.println(dict);

      System.out.println((System.nanoTime() - time) / 1000_000_000.0);
    }
  }
}
