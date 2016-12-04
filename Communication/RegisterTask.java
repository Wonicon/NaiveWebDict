package Communication;

import java.io.*;
import java.net.*;

public class RegisterTask extends Task {
  void handle(int taskID, Socket conn, DataInputStream in) {
    try {
      DataOutputStream out = new DataOutputStream(conn.getOutputStream());
      String username = in.readUTF();
      String password = in.readUTF();
      System.out.println(taskID + ".register.username: " + username);
      System.out.println(taskID + ".register.password: " + password);
      // TODO check username collision.
      out.writeUTF(CMD.register());
      out.writeBoolean(true);
    } catch (IOException e) {
      System.err.println("Failed to handle register task");
    }
  }
}
