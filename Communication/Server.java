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
   * The lock on onlineUsers.
   */
  static Lock onlineUsersLock = new ReentrantLock();

  /**
   * Collection of established sessions.
   */
  static List<Session> sessions = new LinkedList<>();

  /**
   * The lock on sessions.
   */
  static Lock sessionsLock = new ReentrantLock();

  /**
   * Count of task, used to distinguish different tasks.
   */
  private static int sessionCount = 0;

  public static void main(String[] args) throws Exception {
    ServerSocket socket = new ServerSocket(8000);
    while (!socket.isClosed()) {
      System.out.println("wait connection");
      Session session = new Session(socket.accept(), sessionCount++);
      new Thread(session).start();
    }
    socket.close();
  }
}
