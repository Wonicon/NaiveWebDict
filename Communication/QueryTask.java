package Communication;

import java.io.*;
import java.net.*;

public class QueryTask extends Task {
  void handle(Socket conn, DataInputStream in) {
    try {
      DataOutputStream out = new DataOutputStream(conn.getOutputStream());
      String username = in.readUTF();
      String word = in.readUTF();
      System.out.println("username: " + username);
      System.out.println("word: " + word);
      // TODO check username existence and password coherence.
      String[] results = { "A", "B", "C" };
      out.writeInt(results.length);
      for (String s: results) {
        out.writeUTF(s);
      }
    } catch (IOException e) {
      System.err.println("Failed to handle register task");
    }
  }
}
