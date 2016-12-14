package Communication;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Client {
  private String username = "";

  public String getUsername() { return username; }

  private int uid = 0;

  /**
   * Notify the <code>serverMsgHandler</code> thread to stop.
   */
  private volatile boolean shutdown = false;

  /**
   * Method for users to shutdown the client elegantly.
   */
  public void stop() {
    shutdown = true;

    // It is practical to stop the listener thread by close the socket.
    // As it might stall on read from stream, the loop boolean value may not work.
    try {
      socket.close();
    }
    catch (IOException e) {
      System.err.println(e.toString());
    }
  }

  @FunctionalInterface
  public interface ArrayCallback {
    void run(int[] array);
  }

  @FunctionalInterface
  public interface BoolCallback {
    void run(boolean cond);
  }

  @FunctionalInterface
  public interface UserListCallback {
    void run(String[] users);
  }

  private ArrayCallback countCallback = null;

  private BoolCallback loginCallback = null;

  private UserListCallback userListCallback = null;

  /**
   * The global connection socket.
   */
  private Socket socket = null;

  private DataInputStream fromServer;

  private DataOutputStream toServer;

  public enum State {
    Start, Register, Login, Logout, List, Like, Online, Count;
  }

  private volatile State state = State.Start;

  public State getState() { return state; }

  private Lock stateLock = new ReentrantLock();

  public void lock() { stateLock.lock(); }

  public void unlock() { stateLock.unlock(); }

  private Condition response = stateLock.newCondition();

  public void await() {
    try {
      response.await();
    }
    catch (InterruptedException e) {
      System.err.println(e.toString());
    }
  }

  /**
   * Connect to server while starting.
   * And create the listening thread.
   */
  public Client() {
    // Establish connection and create stream.
    try {
      socket = new Socket("localhost", 8000);
      fromServer = new DataInputStream(socket.getInputStream());
      toServer = new DataOutputStream(socket.getOutputStream());
    }
    catch (IOException e) {
      System.err.println("Connection failed");
    }

    new Thread(this::serverMsgHandler).start();
  }

  /**
   * Register an account. Auto login.
   *
   * @param username Username
   * @param password Password
   */
  public void register(String username, String password) {
    stateLock.lock();
    try {
      if (state == State.Start) {
        toServer.writeUTF(Message.register);
        toServer.writeUTF(username);
        toServer.writeUTF(password);
        state = State.Register;
      }
      else {
        System.out.println("No need to register");
      }
    }
    catch (IOException e) {
      System.err.println("Failed to send register request");
    }
    finally {
      stateLock.unlock();
    }
  }

  /**
   * Log into an account
   *
   * @param username Username
   * @param password Password
   */
  public void login(String username, String password, BoolCallback callback) {
    stateLock.lock();
    try {
      if (state == State.Start) {
        toServer.writeUTF(Message.login);
        toServer.writeUTF(username);
        toServer.writeUTF(password);
        this.username = username;
        state = State.Login;
        loginCallback = callback;
      }
      else {
        System.out.println("Logout first or wait other request to be answered");
      }
    }
    catch (IOException e) {
      System.err.println("Failed to send login request");
    }
    finally {
      stateLock.unlock();
    }
  }

  /**
   * Log out an account
   */
  public void logout() {
    stateLock.lock();
    try {
      if (state == State.Online) {
        toServer.writeUTF(Message.logout);
        state = State.Logout;
      }
      else {
        System.out.println("Login first or wait other request to be answered");
      }
    }
    catch (IOException e) {
      System.err.println("Failed to send logout request");
    }
    finally {
      stateLock.unlock();
    }
  }

  /**
   * Request online user list.
   * @param userListCallback
   */
  public void list(UserListCallback userListCallback) {
    stateLock.lock();
    try {
      toServer.writeUTF(Message.list);
      state = State.List;
      this.userListCallback = userListCallback;
    }
    catch (IOException e) {
      System.err.println("Failed to send list request");
    }
    finally {
      stateLock.unlock();
    }
  }

  public void like(String word, int dict) {
    stateLock.lock();
    try {
      toServer.writeUTF(Message.like);
      toServer.writeUTF(word);
      toServer.writeInt(uid);
      toServer.writeInt(dict);
      state = State.Like;
    }
    catch (IOException e) {
      System.err.println(e.toString());
    }
    finally {
      stateLock.unlock();
    }
  }

  /**
   * Send count request.
   * @param word The word to query.
   * @param dict The dictionaries that need count number.
   */
  public void count(String word, int[] dict, ArrayCallback callback) {
    stateLock.lock();
    try {
      toServer.writeUTF(Message.count);
      toServer.writeUTF(word);
      toServer.writeInt(dict.length);
      for (int id : dict) {
        toServer.writeInt(id);
      }
      state = State.Count;
      countCallback = callback;
    }
    catch (IOException e) {
      System.err.println(e.toString());
    }
    finally {
      stateLock.unlock();
    }
  }

  /**
   * This method acts as a stand-alone thread to receive server's pushing messages and responses.
   */
  private void serverMsgHandler() {
    String cmd;
    while (!shutdown) {
      try {
        cmd = fromServer.readUTF();
      }
      catch (IOException e) {
        System.err.println("Cannot receive server message");
        break;
      }

      stateLock.lock();
      System.out.println("receive cmd " + cmd);
      try {
        switch (cmd) {
          // Handle push message.
          case Message.notifyLogin:
            System.out.println(fromServer.readUTF() + " has logged in.");
            break;
          case Message.notifyLogout:
            System.out.println(fromServer.readUTF() + " has logged out.");
            break;
          // Handle response message.
          case Message.register:
            assert state == State.Register;
            boolean result = fromServer.readBoolean();
            System.out.println("registered: " + result);
            state = State.Start;
            response.signal();
            break;
          case Message.login:
            assert state == State.Login;
            uid = fromServer.readInt();
            if (uid > 0) {
              System.out.println("login as " + username);
              state = State.Online;
            }
            else {
              System.out.println("login failed");
              username = "";
              state = State.Start;
            }
            if (loginCallback != null) {
              loginCallback.run(uid > 0);
            }
            response.signal();
            break;
          case Message.logout:
            assert state == State.Logout;
            state = State.Start;
            response.signal();
            break;
          case Message.list:
            assert state == State.List;
            int n = fromServer.readInt();
            String[] users = new String[n];
            for (int i = 0; i < n; i++) {
              users[i] = fromServer.readUTF();
              System.out.println(users[i]);
            }
            state = State.Online;
            response.signal();
            if (userListCallback != null) {
              userListCallback.run(users);
            }
            break;
          case Message.count:
            assert state == State.Count;
            int[] counts = new int[fromServer.readInt()];
            for (int i = 0; i < counts.length; i++) {
              counts[i] = fromServer.readInt();
            }
            countCallback.run(counts);
            state = State.Online;
            response.signal();
            break;
        }
      }
      catch (IOException e) {
        System.err.println("Cannot receive server message");
        break;
      }
      finally {
        stateLock.unlock();
      }
    }
  }

  public static void main(String[] args) throws Exception {
    Client inst = new Client();

    System.out.print(inst.username + "> ");
    Scanner input = new Scanner(System.in);
    while (input.hasNext()) {
      String[] arg = input.nextLine().split(" ");
      String cmd = arg[0];

      // Route to different request sender.
      switch (cmd) {
        case Message.register:
          inst.register(arg[1], arg[2]);
          break;
        case Message.login:
          inst.login(arg[1], arg[2], null);
          break;
        case Message.logout:
          inst.logout();
          break;
        case Message.list:
          inst.list(null);
          break;
      }

      inst.lock();
      if (inst.getState() != State.Start && inst.getState() != State.Online) {
        State old = inst.getState();
        while (inst.getState() == old) {
          inst.await();
        }
      }
      inst.unlock();

      System.out.print(inst.username + "> ");
    }
  }
}
