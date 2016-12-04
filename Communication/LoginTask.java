package Communication;

import java.io.*;
import java.net.*;

public class LoginTask extends Task {
  void handle(int taskID, Socket conn, DataInputStream in) {
    try {
      DataOutputStream out = new DataOutputStream(conn.getOutputStream());
      String username = in.readUTF();
      String password = in.readUTF();
      System.out.println(taskID + ".login.username: " + username);
      System.out.println(taskID + ".login.password: " + password);

      // TODO check username existence and password coherence.
      String token = "...";
      Server.onlineUsersLock.lock();
      if (!Server.onlineUsers.contains(username)) {
        Server.onlineUsers.add(username);
        token = username;
      }
      Server.onlineUsersLock.unlock();

      out.writeUTF(CMD.login());
      out.writeUTF(token);
    }
    catch (IOException e) {
      System.err.println("Failed to handle register for task " + taskID);
    }
  }
}
