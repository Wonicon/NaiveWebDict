package Communication;

import java.io.*;
import java.net.*;

public class LogoutTask extends Task {
  void handle(int taskID, Socket conn, DataInputStream in) {
    try {
      String username = in.readUTF();
      System.out.println(taskID + ".logout.username: " + username);
      // TODO check username existence and password coherence.
      Server.onlineUsersLock.lock();
      Server.onlineUsers.remove(username);
      Server.onlineUsersLock.unlock();
    } catch (IOException e) {
      System.err.println("Failed to handle register for task " + taskID);
    }
  }
}
