package DB;

import Communication.WordCardMessage;

import java.sql.*;
import java.util.ArrayList;

public class Database {
  private final static String DBDRIVER = "org.gjt.mm.mysql.Driver";

  private final static String database = "DICT";

  private final static String url = "jdbc:mysql://localhost:3306/" + database + "?useUnicode=yes&characterEncoding=UTF-8";

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
   * Describe a class of procedure that may throw SQLException
   */
  @FunctionalInterface
  private interface SQLProcedure<T> {
    T run(Statement stmt) throws SQLException;
  }

  /**
   * A decorator that performs the common procedure to build connection to database.
   * @param callback The actual sql logic.
   */
  private <T> T sqlContext(T defaultValue, SQLProcedure<T> callback) {
    try (
        Connection conn = DriverManager.getConnection(url, user, control_password);
        Statement stmt = conn.createStatement()
    ) {
      return callback.run(stmt);
    }
    catch (SQLException e) {
      System.err.println(e.toString());
    }
    return defaultValue;
  }

  /**
   * Insert a new user record into the user table
   * @param username Unique username.
   * @param password Password in plain text.
   * @return True if registered, false if the username exists.
   */
  public boolean register(String username, String password) { return sqlContext(false, stmt -> {
    String sql = "select password from user where username='" + username + "'";
    try (ResultSet rs = stmt.executeQuery(sql)) {
      if (rs.next()) {
        System.out.println("User " + username + " already exists.");
        return false;
      }
      else {
        stmt.executeUpdate(String.format(
            "insert into user (username, password, login) values ('%s', '%s', false)", username, password
        ));
        return true;
      }
    }
  });}

  /**
   * Check password to allow login, and update the login state.
   * @param username Unique username.
   * @param password Password in plain text.
   * @return The unique uid to identify the user for future requests. 0 means login failed.
   */
  public int login(String username, String password) { return sqlContext(0, stmt -> {
    String sql = "select uid, password, login from user where username='" + username + "'";
    try (ResultSet rs = stmt.executeQuery(sql)) {
      if (!rs.next()) {
        System.out.println("User " + username + " not found");
        return 0;
      }
      else {
        String realPassword = rs.getString("password");
        int logged = Integer.parseInt(rs.getString("login"));
        int uid = Integer.parseInt(rs.getString("uid"));
        if (realPassword.equals(password) && logged == 0) {
          stmt.executeUpdate("update user set login=true where username='" + username + "'");
          System.out.println("User " + username + " login successfully");
          return uid;
        }
        else {
          System.out.println("User " + username + "'s password is not correct");
          return 0;
        }
      }
    }
  });}

  public void logout(int uid) { sqlContext(0,stmt ->
      stmt.executeUpdate("update user set login=false where uid=" + uid)
  );}

  public void likeWord(String word, int uid, int source) { sqlContext(false, stmt -> {
    try (ResultSet rs = stmt.executeQuery("select * from user_like where uid=" + uid + " and word='" + word + "' and dict_id=" + source)) {
      if (!rs.next()) {  // 判断是否点赞
        stmt.executeUpdate("insert into user_like (word, uid, dict_id) values('" + word + "'," + uid + "," + source + ")");
        if (!stmt.executeQuery("select * from count where word='" + word + "' and dict_id=" + source).next()) {
          // 如果从来没有人点过生成新的count
          stmt.executeUpdate("insert count (word, dict_id, count) values('" + word + "'," + source + ",1)");
        }
        else {
          // 已经存在则进行 update
          stmt.executeUpdate("update count set count=count+1 where word='" + word + "' and dict_id=" + source);
        }
        return true;
      }
      else {  // 取消点赞
        stmt.executeUpdate("delete from user_like where uid='" + uid + "' and word='" + word + "' and dict_id=" + source);
        stmt.executeUpdate("update count set count=count-1 where word='" + word + "' and dict_id=" + source);
        return false;
      }
    }
  });}

  /**
   * Retrieve the `like' counts from a sequence of dictionaries.
   * @param word The word to be queried.
   * @param dictID Dictionary id series.
   * @return The `like' count array in the same order of <code>dictID</code>.
   */
  public int[] queryCount(String word, int[] dictID, int uid) { return sqlContext(null, stmt -> {
    int[] counts = new int[dictID.length];
    for (int i = 0; i < dictID.length; i++) {
      String sql = "select count from count where word='" + word + "' and dict_id=" + dictID[i];
      System.out.println(sql);
      try (ResultSet rs = stmt.executeQuery(sql)) {
        if (rs.next()) {
          counts[i] = rs.getInt("count");
          if (queryUser(word, uid, dictID[i])) {
            counts[i] = -counts[i];
          }
        }
        else {
          System.err.println("dict id " + dictID[i] + " not found");
          counts[i] = 0;
        }
      }
    }
    return counts;
  });}

  /**
   * Query whether a user like a word's explanation from a specific dictionary.
   * @param word The word to be queried.
   * @param uid The user's id.
   * @param source The id for the dictionary.
   * @return <code>true</code> if the entry exists, <code>false</code> otherwise.
   */
  public boolean queryUser(String word, int uid, int source) { return sqlContext(false, stmt -> {
    String sql = "select * from user_like where uid=" + uid + " and word='" + word + "' and dict_id=" + source;
    return stmt.executeQuery(sql).next();
  });}

  public void insertWordCard(String sender, String receiver, String content) { sqlContext(0, stmt ->
      stmt.executeUpdate("insert into word_card (sender, receiver, content, received)"
          + "values ('" + sender + "','" + receiver + "','" + content + "', false)")
  );}

  public ArrayList<WordCardMessage> getUnreceivedWordCard(String receiver) {
    ArrayList<WordCardMessage> messages = new ArrayList<>();
    return sqlContext(messages, stmt -> {
    String sql = String.format("select * from word_card where receiver='%s' and received=false", receiver);
    try (ResultSet rs = stmt.executeQuery(sql)) {
      while (rs.next()) {
        messages.add(new WordCardMessage(rs.getInt("card_id"), rs.getString("sender"), rs.getString("receiver"), rs.getString("content")));
      }
    }
    return messages;
  });}

  public void confirmReceived(int cardID) { sqlContext(false, stmt -> {
    stmt.executeUpdate("update word_card set received=true where card_id=" + cardID);
    return true;
  });}
}