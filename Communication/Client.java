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

  private int uid = 0;

  /**
   * The global connection socket.
   */
  private Socket socket = null;

  private DataInputStream fromServer;

  private DataOutputStream toServer;

  private enum State {
    Start, Register, Login, Logout, Query, List, Online
  }

  private volatile State state = State.Start;

  private Lock stateLock = new ReentrantLock();

  private Condition response = stateLock.newCondition();

  /**
   * Not allow instantiation.
   */
  public Client() {
  }

  /**
   * Register an account. Auto login.
   *
   * @param username Username
   * @param password Password
   */
  public void register(String username, String password) {
    try {
      toServer.writeUTF(CMD.register());
      toServer.writeUTF(username);
      toServer.writeUTF(password);
    }
    catch (IOException e) {
      System.err.println("Failed to send register request");
    }
  }

  /**
   * Log into an account
   *
   * @param username Username
   * @param password Password
   */
  public void login(String username, String password) {
    try {
      this.username = username;
      toServer.writeUTF(CMD.login());
      toServer.writeUTF(username);
      toServer.writeUTF(password);
    }
    catch (IOException e) {
      System.err.println("Failed to send login request");
    }
  }

  /**
   * Log out an account
   */
  public void logout() {
    try {
      DataOutputStream out = new DataOutputStream(socket.getOutputStream());
      out.writeUTF(CMD.logout());
      out.writeInt(uid);
    }
    catch (IOException e) {
      System.err.println("Failed to send logout request");
    }
  }

  /**
   * Query a word and get 'like count'
   * The like count for multiple dictionary are managed like following:
   * { "dict_name:count", ... }
   *
   * @param word Word to query.
   */
  public void query(String word) {
    String[] results = null;
    try {
      toServer.writeUTF(CMD.query());
      toServer.writeInt(uid);
      toServer.writeUTF(word);
    }
    catch (IOException e) {
      System.err.println("Failed to send query request");
    }
  }

  public void list() {
    try {
      toServer.writeUTF(CMD.list());
    }
    catch (IOException e) {
      System.err.println("Failed to send list request");
    }
  }

  public void serverMsgHandler() {
    String cmd;
    while (true) {
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
        // TODO Handle push message.
        if (cmd.equals(CMD.notifyLogin())) {
          System.out.println(fromServer.readUTF() + " has logged in.");
        }
        else if (cmd.equals(CMD.notifyLogout())) {
          System.out.println(fromServer.readUTF() + " has logged out.");
        }
        // TODO Handle response message.
        else if (cmd.equals(CMD.register())) {
          assert state == State.Register;
          boolean result = fromServer.readBoolean();
          System.out.println("registered: " + result);
          state = State.Start;
          response.signal();
        }
        else if (cmd.equals(CMD.login())) {
          assert state == State.Login;
          uid = fromServer.readInt();
          if (uid > 0) {
            System.out.println("login as " + username);
          }
          else {
            System.out.println("login failed");
            username = "";
          }
          state = State.Online;
          response.signal();
        }
        else if (cmd.equals(CMD.logout())) {
          assert state == State.Logout;
          state = State.Start;
          response.signal();
        }
        else if (cmd.equals(CMD.query())) {
          assert state == State.Query;
          int n = fromServer.readInt();
          for (int i = 0; i < n; i++) {
            System.out.println(fromServer.readUTF());
          }
          state = State.Online;
          response.signal();
        }
        else if (cmd.equals(CMD.list())) {
          assert state == State.List;
          int n = fromServer.readInt();
          for (int i = 0; i < n; i++) {
            System.out.println(fromServer.readUTF());
          }
          state = State.Online;
          response.signal();
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

  public void run() {
    // Establish connection and create stream.
    try {
      socket = new Socket("localhost", 8000);
      fromServer = new DataInputStream(socket.getInputStream());
      toServer = new DataOutputStream(socket.getOutputStream());
    }
    catch (IOException e) {
      System.err.println("Connection failed");
    }

    Thread handler = new Thread(this::serverMsgHandler);
    handler.start();

    System.out.print(username + "> ");
    Scanner input = new Scanner(System.in);
    while (input.hasNext()) {
      stateLock.lock();
      try {
        String[] arg = input.nextLine().split(" ");
        String cmd = arg[0];

        // Route to different request sender.
        if (cmd.equals(CMD.register())) {
          register(arg[1], arg[2]);
          state = State.Register;
        }
        else if (cmd.equals(CMD.login())) {
          if (state != State.Online) {
            login(arg[1], arg[2]);
            state = State.Login;
          }
          else {
            System.out.println("logout first!");
          }
        }
        else if (cmd.equals(CMD.query())) {
          query(arg[1]);
          state = State.Query;
        }
        else if (cmd.equals(CMD.logout())) {
          if (state == State.Online) {
            logout();
            state = State.Logout;
          }
          else {
            System.out.println("login first!");
          }
        }
        else if (cmd.equals(CMD.list())) {
          list();
          state = State.List;
        }

        if (state != State.Start && state != State.Online) {
          State old = state;
          while (state == old) {
            response.await();
          }
        }
      }
      catch (InterruptedException e) {
        System.err.println(e.toString());
      }
      finally {
        stateLock.unlock();
      }

      System.out.print(username + "> ");
    }

    try {
      handler.join();
    }
    catch (InterruptedException e) {
      System.err.println(e.toString());
    }
  }

  public static void main(String[] args) throws Exception {
    new Client().run();
  }
}
