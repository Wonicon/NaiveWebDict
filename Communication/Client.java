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
   * Query a word and get definitions.
   * @param word Word to query.
   * @return Definitions.
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

  public static void main(String[] args) throws Exception {
    System.out.println("register:" + register(args[0], args[1]));
    System.out.println("login:" + login(args[0], args[1]));
    for (String s: query(new Scanner(System.in).next())) {
      System.out.println(s);
    }
    getSocket().close();
  }
}
