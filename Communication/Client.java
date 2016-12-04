package Communication;

import java.net.*;
import java.io.*;
import java.util.*;

public class Client {
  /**
   * The global connection socket.
   */
  private static Socket socket_ = null;

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
   * Create connection when need.
   * @return The established connection's socket.
   */
  private static Socket getSocket() {
    if (socket_ == null) {
      try {
        socket_ = new Socket("localhost", 8000);
      } catch (IOException e) {
        System.err.println("Connection failed");
      }
    }
    return socket_;
  }

  private static void setToken(boolean auth, String uid) {
    assert token == null;
    if (auth) {
      token = uid;
    }
  }

  /**
   * Register an account. Auto login.
   * @param username Username
   * @param password Password
   * @return True if the registration is successful, false otherwise.
   */
  public static boolean register(String username, String password) {
    Socket socket = getSocket();
    Boolean auth = false;
    try {
      DataOutputStream out = new DataOutputStream(socket.getOutputStream());
      out.writeUTF(CMD.register());
      out.writeUTF(username);
      out.writeUTF(password);
      DataInputStream in = new DataInputStream(socket.getInputStream());
      auth = in.readBoolean();
    } catch (IOException e) {
      System.err.println("Connection failed, cannot register");
    }
    setToken(auth, username);
    return auth;
  }

  /**
   * Log into an account
   * @param username Username
   * @param password Password
   * @return True if the authentication is successful, false otherwise.
   */
  public static boolean login(String username, String password) {
    Socket socket = getSocket();
    Boolean auth = false;
    try {
      DataOutputStream out = new DataOutputStream(socket.getOutputStream());
      out.writeUTF(CMD.login());
      out.writeUTF(username);
      out.writeUTF(password);
      DataInputStream in = new DataInputStream(socket.getInputStream());
      auth = in.readBoolean();
    } catch (IOException e) {
      System.err.println("Connection failed, cannot login");
    }
    setToken(auth, username);
    return auth;
  }

  /**
   * Log out an account
   */
  public static void logout() {
    Socket socket = getSocket();
    try {
      DataOutputStream out = new DataOutputStream(socket.getOutputStream());
      out.writeUTF(CMD.logout());
      out.writeUTF(token);
    } catch (IOException e) {
      System.err.println("Connection failed, cannot logout");
    }
  }

  /**
   * Query a word and get 'like count'
   * The like count for multiple dictionary are managed like following:
   *   { "dict_name:count", ... }
   * @param word Word to query.
   * @return like counts, in specific format.
   */
  public static String[] query(String word) {
    Socket socket = getSocket();
    String[] results = null;
    try {
      DataOutputStream out = new DataOutputStream(socket.getOutputStream());
      out.writeUTF(CMD.query());
      out.writeUTF(token);
      out.writeUTF(word);
      DataInputStream in = new DataInputStream(socket.getInputStream());
      results = new String[in.readInt()];
      System.out.println("results [" + results.length + "]");
      for (int i = 0; i < results.length; i++) {
        results[i] = in.readUTF();
      }
    } catch (IOException e) {
      System.err.println("Query failed");
    }

    return results;
  }

  public static String[] list() {
    Socket socket = getSocket();
    String[] results = null;

    try {
      DataOutputStream out = new DataOutputStream(socket.getOutputStream());
      DataInputStream in = new DataInputStream(socket.getInputStream());

      out.writeUTF(CMD.list());

      int n = in.readInt();
      results = new String[n];
      for (int i = 0; i < results.length; i++) {
        results[i] = in.readUTF();
      }
    } catch (IOException e) {
      System.err.println("List failed");
    }

    return results;
  }

  public static void main(String[] args) throws Exception {
    Scanner input = new Scanner(System.in);
    while (input.hasNext()) {
      String[] arg = input.nextLine().split(" ");
      String cmd = arg[0];

      // Route to different request sender.
      if (cmd.equals(CMD.register())) {
        System.out.println("register:" + register(arg[1], arg[2]));
      } else if (cmd.equals(CMD.login())) {
        System.out.println("login:" + login(arg[1], arg[2]));
      } else if (cmd.equals(CMD.query())) {
        for (String s : query(arg[1])) {
          System.out.println(s);
        }
      } else if (cmd.equals(CMD.logout())) {
        logout();
      } else if (cmd.equals(CMD.list())) {
        for (String user: list()) {
          System.out.println(user);
        }
      }
    }
  }
}
