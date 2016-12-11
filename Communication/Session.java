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
  private enum State { OFFLINE, ONLINE };

  private State state = State.OFFLINE;

  private String username = "";

  private int uid = 0;

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

    handlerMap.put(Message.register, this::register);
    handlerMap.put(Message.login, this::login);
    handlerMap.put(Message.logout, this::logout);
    handlerMap.put(Message.list, this::list);
    handlerMap.put(Message.like, this::like);
    handlerMap.put(Message.count, this::count);
  }

  private void register() {
    try {
      String username = fromClient.readUTF();
      String password = fromClient.readUTF();
      System.out.println("Session" + sessionID + ":register:" +  username + "&" + password);

      outLock.lock();
      try {
        toClient.writeUTF(Message.register);
        toClient.writeBoolean(Server.db.register(username, password));
      }
      finally {
        outLock.unlock();
      }
    }
    catch (IOException e) {
      System.err.println("Failed to handle register for session " + sessionID);
    }
  }

  private void login() {
    try {
      int result;

      String username = fromClient.readUTF();
      String password = fromClient.readUTF();

      if (state != State.ONLINE) {
        System.out.println(sessionID + ":login:" + username + "&" + password);
        // Check username existence and password coherence and then get the uid.
        result = uid = Server.db.login(username, password);
        // Allow logged-in user to receive pushing message from server.
        if (uid > 0) {
          // TODO This is too coarse. Try to use queue.
          this.username = username;
          state = State.ONLINE;
          Server.sessionsLock.lock();
          for (Session s : Server.sessions) {
            s.notifyLogin(username);
          }
          Server.sessions.add(this);
          Server.sessionsLock.unlock();
        }
      }
      else {
        System.out.println(sessionID + ":login:duplicated");
        result = -1;
      }

      // Send response message.
      outLock.lock();
      try {
        toClient.writeUTF(Message.login);
        toClient.writeInt(result);
      }
      finally {
        outLock.unlock();
      }
    }
    catch (IOException e) {
      System.err.println("Failed to handle register for task " + sessionID);
    }
  }

  private void logout() {
    try {
      if (state == State.ONLINE) {
        System.out.println(sessionID + ":logout:uid" + uid);
        Server.db.logout(uid);
        // Update sessions
        Server.sessionsLock.lock();
        Server.sessions.remove(this);
        for (Session s : Server.sessions) {
          s.notifyLogout(username);
        }
        Server.sessionsLock.unlock();
        state = State.OFFLINE;
        username = "";
        uid = 0;
      }
      else {
        System.out.println("Session" + sessionID + ": invalid logout request.");
      }

      outLock.lock();
      try {
        toClient.writeUTF(Message.logout);
      }
      finally {
        outLock.unlock();
      }
    }
    catch (IOException e) {
      System.err.println("Failed to handle register for session " + sessionID);
    }
  }

  private void like() {
    try {
      String word = fromClient.readUTF();
      int uid = fromClient.readInt();
      int dict = fromClient.readInt();
      Server.db.likeWord(word, uid, dict);
      toClient.writeUTF(Message.like);
    }
    catch (IOException e) {
      System.err.println(e.toString());
    }
  }

  /**
   * Service count request.
   *
   * -> CMD.count, word, n, id1, id2, ..., idn
   * <- CMD.count, n, n1, n2, ..., nn.
   */
  private void count() {
    // Receive
    try {
      String word = fromClient.readUTF();
      int n = fromClient.readInt();
      int[] dicts = new int[n];
      for (int i = 0; i < dicts.length; i++) {
        dicts[i] = fromClient.readInt();
      }
      int[] counts = Server.db.queryCount(word, dicts, uid);
      // Send
      outLock.lock();
      try {
        toClient.writeUTF(Message.count);
        toClient.writeInt(counts.length);
        for (int cnt : counts) {
          toClient.writeInt(cnt);
        }
      }
      catch (IOException e) {
        System.err.println(e.toString());
      }
      finally {
        outLock.unlock();
      }
    }
    catch (IOException e) {
      System.err.println(e.toString());
    }
  }

  /**
   * Retrieve the current online users' username from <code>Server.sessions</code> collection.
   * This method is not thread safe, guard it with synchronizing lock.
   * @return The array of online username.
   */
  private String[] getOnlineList() {
    String[] users;
    users = new String[Server.sessions.size()];
    int i = 0;
    for (Session session : Server.sessions) {
      users[i++] = session.username;
      assert session.uid > 0;
    }
    return users;
  }


  private void list() {
    Server.sessionsLock.lock();
    String[] list = getOnlineList();
    Server.sessionsLock.unlock();

    outLock.lock();
    try {
      // Send online user list
      toClient.writeUTF(Message.list);
      toClient.writeInt(list.length);
      for (String user : list) {
        toClient.writeUTF(user);
      }
    }
    catch (IOException e) {
      System.err.println("Failed to handle register for session " + sessionID);
    }
    finally {
      outLock.unlock();
    }
  }

  private void notifyLogin(String newUser) {
    outLock.lock();
    try {
      toClient.writeUTF(Message.notifyLogin);
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
      toClient.writeUTF(Message.notifyLogout);
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
          System.out.println("Message " + cmd + " not found");
        }
      }
      catch (IOException e) {
        System.err.println("Session " + sessionID + " failed to communicate: " + e.toString());
        if (uid != 0) {
          logout();
        }
        break;
      }
    }
  }
}
