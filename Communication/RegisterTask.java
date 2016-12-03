package Communication;

import java.io.*;
import java.net.*;

public class RegisterTask extends Task {
  void handle(Socket conn, DataInputStream in) {
    try {
      DataOutputStream out = new DataOutputStream(conn.getOutputStream());
      String username = in.readUTF();
      String password = in.readUTF();
      System.out.println("username: " + username);
      System.out.println("password: " + password);
      // TODO check username collision.
      out.writeBoolean(true);
    } catch (IOException e) {
      System.err.println("Failed to handle register task");
    }
  }
}
