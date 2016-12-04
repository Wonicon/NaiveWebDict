package Communication;

import java.net.*;
import java.util.*;
import java.util.concurrent.locks.*;

public class Server {
  /**
   * Cache of users logged in.
   */
  static Set<String> onlineUsers = new TreeSet<>();

  /**
   * The lock on onlineUsers
   */
  static Lock onlineUsersLock = new ReentrantLock();

  /**
   * Count of task, used to distinguish different tasks.
   */
  private static int sessionCount = 0;

  public static void main(String[] args) throws Exception {
    ServerSocket socket = new ServerSocket(8000);
    while (!socket.isClosed()) {
      System.out.println("wait connection");
      Socket conn = socket.accept();
      new Thread(new Session(conn, sessionCount++)).start();
    }
    socket.close();
  }
}
