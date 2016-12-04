package Communication;

import java.io.*;
import java.net.*;

public class QueryTask extends Task {
  void handle(int taskID, Socket conn, DataInputStream in) {
    try {
      DataOutputStream out = new DataOutputStream(conn.getOutputStream());
      String username = in.readUTF();
      String word = in.readUTF();
      System.out.println(taskID + ".query.username: " + username);
      System.out.println(taskID + ".query.word: " + word);
      // TODO check username existence and password coherence.
      String[] results = { "A", "B", "C" };
      out.writeUTF(CMD.query());
      out.writeInt(results.length);
      for (String s: results) {
        out.writeUTF(s);
      }
    } catch (IOException e) {
      System.err.println("Failed to handle register task");
    }
  }
}
