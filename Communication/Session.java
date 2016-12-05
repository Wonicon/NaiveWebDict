package Communication;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Session class refers to a specific connection to client.
 * It records the connection socket along with its in/out stream,
 * maintaining synchronizing lock and handling request from client.
 */
class Session implements Runnable {
  private Socket socket;

  private DataInputStream fromClient;

  private DataOutputStream toClient;

  Lock outLock = new ReentrantLock();

  private int sessionID;

  /**
   * Index of handlers for different tasks.
   */
  private Map<String, Runnable> handlerMap = new HashMap<>();

  Session(Socket socket, int sessionID) {
    this.socket = socket;
    this.sessionID = sessionID;
    try {
      fromClient = new DataInputStream(socket.getInputStream());
      toClient = new DataOutputStream(socket.getOutputStream());
    }
    catch (IOException e) {
      System.err.println("Session " + sessionID + ": cannot get in/out stream.");
    }

    handlerMap.put(CMD.register(), this::register);
    handlerMap.put(CMD.login(), this::login);
    handlerMap.put(CMD.query(), this::query);
    handlerMap.put(CMD.logout(), this::logout);
    handlerMap.put(CMD.list(), this::list);
  }

  private void register() {
    try {
      // Get online user list
      Server.onlineUsersLock.lock();
      String[] list = Server.onlineUsers.toArray(new String[Server.onlineUsers.size()]);
      Server.onlineUsersLock.unlock();

      // Send online user list
      toClient.writeUTF(CMD.list());
      toClient.writeInt(list.length);
      for (String user : list) {
        toClient.writeUTF(user);
      }
    }
    catch (IOException e) {
      System.err.println("Failed to handle register for session " + sessionID);
    }
  }

  private void login() {
    try {
      String username = fromClient.readUTF();
      String password = fromClient.readUTF();
      System.out.println(sessionID + ".login.username: " + username);
      System.out.println(sessionID + ".login.password: " + password);

      // TODO check username existence and password coherence.
      String token = "...";
      Server.onlineUsersLock.lock();
      if (!Server.onlineUsers.contains(username)) {
        Server.onlineUsers.add(username);
        token = username;
        // TODO This is too coarse. Try to use queue.
        Server.sessionsLock.lock();
        for (Session s : Server.sessions) {
          s.notifyLogin(username);
        }
        Server.sessions.add(this);
        Server.sessionsLock.unlock();
      }
      Server.onlineUsersLock.unlock();

      toClient.writeUTF(CMD.login());
      toClient.writeUTF(token);
    }
    catch (IOException e) {
      System.err.println("Failed to handle register for task " + sessionID);
    }
  }

  private void logout() {
    try {
      String username = fromClient.readUTF();
      System.out.println(sessionID + ".logout.username: " + username);
      // TODO check username existence and password coherence.
      toClient.writeUTF(CMD.logout());
      // Update online user list
      Server.onlineUsersLock.lock();
      Server.onlineUsers.remove(username);
      Server.onlineUsersLock.unlock();
      // Update sessions
      Server.sessionsLock.lock();
      Server.sessions.remove(this);
      for (Session s : Server.sessions) {
        s.notifyLogout(username);
      }
      Server.sessionsLock.unlock();

    }
    catch (IOException e) {
      System.err.println("Failed to handle register for session " + sessionID);
    }
  }

  private void query() {
    try {
      String username = fromClient.readUTF();
      String word = fromClient.readUTF();
      System.out.println(sessionID + ".query.username: " + username);
      System.out.println(sessionID + ".query.word: " + word);
      // TODO check username existence and password coherence.
      String[] results = {"A", "B", "C"};
      toClient.writeUTF(CMD.query());
      toClient.writeInt(results.length);
      for (String s : results) {
        toClient.writeUTF(s);
      }
    }
    catch (IOException e) {
      System.err.println("Failed to handle register task");
    }
  }

  private void list() {
    try {
      // Get online user list
      Server.onlineUsersLock.lock();
      String[] list = Server.onlineUsers.toArray(new String[Server.onlineUsers.size()]);
      Server.onlineUsersLock.unlock();

      // Send online user list
      toClient.writeUTF(CMD.list());
      toClient.writeInt(list.length);
      for (String user : list) {
        toClient.writeUTF(user);
      }
    }
    catch (IOException e) {
      System.err.println("Failed to handle register for session " + sessionID);
    }
  }

  private void notifyLogin(String newUser) {
    outLock.lock();
    try {
      toClient.writeUTF(CMD.notifyLogin());
      toClient.writeUTF(newUser);
    }
    catch (IOException e) {
      System.err.println("Failed to notify session " + sessionID);
    }
    finally {
      outLock.unlock();
    }
  }

  private void notifyLogout(String leavingUser) {
    outLock.lock();
    try {
      toClient.writeUTF(CMD.notifyLogout());
      toClient.writeUTF(leavingUser);
    }
    catch (IOException e) {
      System.err.println("Failed to notify session " + sessionID);
    }
    finally {
      outLock.unlock();
    }
  }

  @Override
  public void run() {
    while (true) {
      try {
        String cmd = fromClient.readUTF();
        Runnable handler = handlerMap.get(cmd);
        if (handler != null) {
          handler.run();
        }
        else {
          System.out.println("CMD " + cmd + " not found");
        }
      }
      catch (IOException e) {
        System.err.println("Session " + sessionID + ": failed to communicate");
        break;
      }
    }
  }
}
