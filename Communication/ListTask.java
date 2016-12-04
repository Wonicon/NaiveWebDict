package Communication;

import java.io.*;
import java.net.*;

public class ListTask extends Task {
  void handle(int taskID, Socket conn, DataInputStream in) {
    try {
      // Get online user list
      Server.onlineUsersLock.lock();
      String[] list = Server.onlineUsers.toArray(new String[Server.onlineUsers.size()]);
      Server.onlineUsersLock.unlock();

      // Send online user list
      DataOutputStream out = new DataOutputStream(conn.getOutputStream());
      out.writeUTF(CMD.list());
      out.writeInt(list.length);
      for (String user: list) {
        out.writeUTF(user);
      }
    } catch (IOException e) {
      System.err.println("Failed to handle register for task " + taskID);
    }
  }
}
