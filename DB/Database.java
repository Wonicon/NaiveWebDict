package DB;

import java.sql.*;

public class Database {
  private final static String DBDRIVER = "org.gjt.mm.mysql.Driver";

  private final static String database = "DICT";

  private final static String url = "jdbc:mysql://localhost:3306/" + database;

  private String user = null;

  private String control_password = null;

  public Database(String user, String password) {
    this.user = user;
    this.control_password = password;

    // Load database driver.
    try {
      Class.forName(DBDRIVER);
    }
    catch (ClassNotFoundException e) {
      System.err.println("Cannot load mysql driver " + DBDRIVER);
    }
  }

  /**
   * Insert a new user record into the user table
   * @param username Unique username.
   * @param password Password in plain text.
   * @return True if registered, false if the username exists.
   */
  public boolean register(String username, String password) {
    boolean result = false;
    String sql = "select password from user where username=\"" + username + "\";";
    try (
        Connection conn = DriverManager.getConnection(url, user, control_password);
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery(sql)
    ) {
      if (rs.next()) {
        System.out.println("User " + username + " already exists.");
      }
      else {
        stmt.executeUpdate(String.format(
            "insert into user (username, password, login) values ('%s', '%s', false);",
            username, password
        ));
        result = true;
      }
    }
    catch (SQLException e) {
      System.err.println("Failed to execute register query for " + username);
      System.err.println(e.toString());
    }
    return result;
  }

  /**
   * Check password to allow login, and update the login state.
   * @param username Unique username.
   * @param password Password in plain text.
   * @return The unique uid to identify the user for future requests. 0 means login failed.
   */
  public int login(String username, String password) {
    int uid = 0;
    String sql = "select uid, password, login from user where username=\"" + username + "\";";
    try (
        Connection conn = DriverManager.getConnection(url, user, control_password);
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery(sql)
    ) {
      if (!rs.next()) {
        System.out.println("User " + username + " not found");
      }
      else {
        String realPassword = rs.getString("password");
        int logged = Integer.parseInt(rs.getString("login"));
        int uidTemp = Integer.parseInt(rs.getString("uid"));
        if (realPassword.equals(password) && logged == 0) {
          stmt.executeUpdate("update user set login=1 where username=\"" + username + "\";");
          uid = uidTemp;
          System.out.println("User " + username + " login successfully");
        }
        else {
          System.out.println("User " + username + "'s password is not correct");
        }
      }
    }
    catch (SQLException e) {
      System.err.println("Failed to execute login query for " + username + ": " + e.toString());
    }
    return uid;
  }

  public void logout(int uid) {
    try (
        Connection conn = DriverManager.getConnection(url, user, control_password);
        Statement stmt = conn.createStatement()
    ) {
      stmt.executeUpdate("update user set login=false where uid=\"" + uid + "\";");
    }
    catch (SQLException e) {
      System.err.println("Failed to execute login query for uid" + uid + ": " + e.toString());
    }
  }

  public boolean likeWord(String like_word, int uid, int source) {
    boolean result = false;
    try (
        Connection conn = DriverManager.getConnection(url, user, control_password);
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery("select * from user_like where uid=\"" + uid + "\" and word=\"" + like_word + "\" and dict_id=" + source + ";")
    ) {
      if (!rs.next()) {  // 判断是否点赞
        stmt.executeUpdate("insert user_like (word, uid, dict_id) values(\"" + like_word + "\",\"" + uid + "\"," + source + ";");
        String sql = "select * from count where word=\"" + like_word + "\" and source=" + source + ";";
        if (!stmt.executeQuery(sql).next()) { // 如果从来没有人点过生成新的count
          stmt.executeUpdate("insert count (word, dict_id, count) values(\"" + like_word + "\"," + source + ",1;");
        }
        else {  // 已经存在则进行 update
          stmt.executeUpdate("update count set count=count+1 where word=\"" + like_word + "\" and dict_id=" + source + ";");
        }
        result = true;  // 成功点赞
      }
      else {  // 点赞失败
        System.out.println("Duplicated like for '" + like_word + "' by " + uid);
      }
    }
    catch (SQLException e) {
      System.err.println(e.toString());
    }

    return result;
  }

  public boolean dislikeWord(String word, int uid, int source) {
    boolean result = false;
    try (
    Connection conn = DriverManager.getConnection(url, user, control_password);
    Statement stmt = conn.createStatement();
    ResultSet rs = stmt.executeQuery("select * from user_like where uid=\"" + uid + "\" and word=\"" + word + "\" and dict_id=" + source + ";")
    ) {
      if (rs.next()) { // 判断是否点赞
        stmt.executeUpdate("delete from user_like where uid=\"" + uid + "\" and word=\"" + word + "\" and dict_id=" + source + ";");
        stmt.executeUpdate("update count set count=count-1 where word=\"" + word + "\" and dict_id=" + source + ";");
        rs.close();
        stmt.close();
        conn.close();
        result = true;
      }
    }
    catch (SQLException e) {
      System.err.println(e.toString());
    }

    return result;
  }

  /**
   * Retrieve the `like' counts from a sequence of dictionaries.
   * @param word The word to be queried.
   * @param dictID Dictionary id series.
   * @return The `like' count array in the same order of <code>dictID</code>.
   */
  public int[] queryCount(String word, int[] dictID) {
    int[] counts = new int[dictID.length];
    try (
        Connection conn = DriverManager.getConnection(url, user, control_password);
        Statement stmt = conn.createStatement()
    ) {
      for (int i = 0; i < dictID.length; i++) {
        ResultSet rs = stmt.executeQuery("select count from count where word='" + word + " and dict_id=" + dictID[i]);
        counts[i] = rs.getInt("count");
      }
    }
    catch (SQLException e) {
      System.err.println(e.toString());
    }
    return counts;
  }

  /**
   * Query whether a user like a word's explanation from a specific dictionary.
   * @param word The word to be queried.
   * @param uid The user's id.
   * @param source The id for the dictionary.
   * @return <code>true</code> if the entry exists, <code>false</code> otherwise.
   */
  public boolean queryUser(String word, int uid, int source) {
    boolean result = false;
    try (
        Connection conn = DriverManager.getConnection(url, user, control_password);
        Statement stmt = conn.createStatement()
    ) {
      result = stmt.execute("select * from user_like where uid=\"" + uid + "\" and word=\"" + word + "\" and dict_id=" + source + ";");
    }
    catch (SQLException e) {
      System.err.println(e.toString());
    }
    return result;
  }
}