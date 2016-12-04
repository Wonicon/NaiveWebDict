package Communication;

import java.net.*;
import java.io.*;
import java.util.*;
import java.util.concurrent.locks.*;

public class Client {
  /**
   * The global connection socket.
   */
  private static Socket socket = null;

  private static DataInputStream fromServer;

  private static DataOutputStream toServer;

  private enum State {
    Start, Register, Login, Logout, Query, List, Online
  }

  static volatile State state = State.Start;

  static Lock stateLock = new ReentrantLock();

  static Condition response = stateLock.newCondition();

  /**
   * The unique token to check whether the client is authenticated.
   * Currently it is the unique username.
   */
  private static String token = "...";

  /**
   * Not allow instantiation.
   */
  private Client() {}

  /**
   * Register an account. Auto login.
   * @param username Username
   * @param password Password
   */
  public static void register(String username, String password) {
    Boolean auth = false;
    try {
      toServer.writeUTF(CMD.register());
      toServer.writeUTF(username);
      toServer.writeUTF(password);
    } catch (IOException e) {
      System.err.println("Failed to send register request");
    }
  }

  /**
   * Log into an account
   * @param username Username
   * @param password Password
   * @return True if the authentication is successful, false otherwise.
   */
  public static void login(String username, String password) {
    try {
      toServer.writeUTF(CMD.login());
      toServer.writeUTF(username);
      toServer.writeUTF(password);
    } catch (IOException e) {
      System.err.println("Failed to send login request");
    }
  }

  /**
   * Log out an account
   */
  public static void logout() {
    try {
      DataOutputStream out = new DataOutputStream(socket.getOutputStream());
      out.writeUTF(CMD.logout());
      out.writeUTF(token);
    } catch (IOException e) {
      System.err.println("Failed to send logout request");
    }
  }

  /**
   * Query a word and get 'like count'
   * The like count for multiple dictionary are managed like following:
   *   { "dict_name:count", ... }
   * @param word Word to query.
   */
  public static void query(String word) {
    String[] results = null;
    try {
      toServer.writeUTF(CMD.query());
      toServer.writeUTF(token);
      toServer.writeUTF(word);
    } catch (IOException e) {
      System.err.println("Failed to send query request");
    }
  }

  public static void list() {
    try {
      toServer.writeUTF(CMD.list());
    } catch (IOException e) {
      System.err.println("Failed to send list request");
    }
  }

  public static void serverMsgHandler() {
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
          String result = fromServer.readUTF();
          System.out.println("login as " + result);
          token = result;
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

  public static void main(String[] args) throws Exception {
    // Establish connection and create stream.
    try {
      socket = new Socket("localhost", 8000);
      fromServer = new DataInputStream(socket.getInputStream());
      toServer = new DataOutputStream(socket.getOutputStream());
    } catch (IOException e) {
      System.err.println("Connection failed");
    }

    Thread handler = new Thread(Client::serverMsgHandler);
    handler.start();

    System.out.print(token + "> ");
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
          login(arg[1], arg[2]);
          state = State.Login;
        }
        else if (cmd.equals(CMD.query())) {
          query(arg[1]);
          state = State.Query;
        }
        else if (cmd.equals(CMD.logout())) {
          logout();
          state = State.Logout;
        }
        else if (cmd.equals(CMD.list())) {
          list();
          state = State.List;
        }

        State old = state;
        while (state == old) {
          response.await();
        }
      }
      finally {
        stateLock.unlock();
      }

      System.out.print(token + "> ");
    }

    handler.join();
  }
}
