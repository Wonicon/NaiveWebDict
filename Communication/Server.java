package Communication;

import java.net.*;
import java.io.*;
import java.util.*;

public class Server {
  /**
   * Index of handlers for different tasks.
   */
  static Map<String, Task> taskMap = new HashMap<>();

  /**
   * Count of task, used to distinguish different tasks.
   */
  static int taskCount = 0;

  public static void main(String[] args) throws Exception {
    taskMap.put(CMD.register(), new RegisterTask());
    taskMap.put(CMD.login(), new LoginTask());
    taskMap.put(CMD.query(), new QueryTask());

    ServerSocket socket = new ServerSocket(8000);
    while (!socket.isClosed()) {
      System.out.println("wait connection");
      Socket conn = socket.accept();
      new Thread(() -> {
        try {
          while (!conn.isClosed()) {
            DataInputStream in = new DataInputStream(conn.getInputStream());
            String cmd = in.readUTF();
            Task task = taskMap.get(cmd);
            if (task != null) {
              task.handle(taskCount++, conn, in);
            } else {
              System.out.println("CMD " + cmd + " not found");
              conn.close();
            }
          }
        } catch (IOException e) {
          System.err.println("Connection failed");
        }
      }).start();
    }
    socket.close();
  }
}
