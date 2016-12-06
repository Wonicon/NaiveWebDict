package DB;

import com.sun.org.apache.xpath.internal.operations.Bool;

import java.sql.*;

public class Database {
  final static String DBDRIVER = "org.gjt.mm.mysql.Driver";

  final static String database = "DICT";

  final static String url = "jdbc:mysql://localhost:3306/" + database;

  String user = null;

  String control_password = null;

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
        ResultSet rs = stmt.executeQuery(sql);
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
        ResultSet rs = stmt.executeQuery(sql);
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
        Statement stmt = conn.createStatement();
    ) {
      stmt.executeUpdate("update user set login=false where uid=\"" + uid + "\";");
    }
    catch (SQLException e) {
      System.err.println("Failed to execute login query for uid" + uid + ": " + e.toString());
    }
  }

  //以下的函数还没有完全完成
  public void like_word(String username, String like_word, int source) throws ClassNotFoundException, SQLException {
    try {
      Class.forName(DBDRIVER);
    }
    catch (ClassNotFoundException e) {
      e.printStackTrace();
    }
    Connection conn = DriverManager.getConnection(url, user, control_password);

    // 4.获取Statement对象
    Statement stmt = conn.createStatement();

    // 5.执行SQL语句
    stmt.executeQuery("use naive");

    //这里是有问题的，只关心了词语没有关心点赞的人
    ResultSet rs = stmt.executeQuery("select * from user_like where word=\"" + like_word + "\";");
    if (!rs.next()) { //数据库中只保存被点赞过的词，如果没有被点赞过先生成
      stmt.executeQuery("insert user_like values(\"" + username + "\",\"" + like_word + "\",0,0,0;");
    }
    if (source == 1) { //from bing
      stmt.execute("update user_like set bing=1 where word=\"" + like_word + "\";");
    }
    else if (source == 2) { //from neteast
      stmt.execute("update user_like set neteast=1 where word=\"" + like_word + "\";");
    }
    else if (source == 3) { //from baidu
      stmt.execute("update user_like set baidu=1 where word=\"" + like_word + "\";");
    }

    // 6.处理从数据库中获取到的数据

    // 7.关闭链接,释放资源
    rs.close();
    stmt.close();
    conn.close();
  }

  public void search_like_word(String username, String like_word, int source) throws ClassNotFoundException, SQLException {
    try {
      Class.forName(DBDRIVER);
    }
    catch (ClassNotFoundException e) {
      e.printStackTrace();
    }

    Connection conn = DriverManager.getConnection(url, user, control_password);

    // 4.获取Statement对象
    Statement stmt = conn.createStatement();

    // 5.执行SQL语句
    stmt.executeQuery("use naive");
    ResultSet rs = stmt.executeQuery("select * from user_like where word=\"" + like_word + "\";");
    if (!rs.next()) { //数据库中只保存被点赞过的词，如果没有被点赞过先生成
      stmt.executeQuery("insert user_like values(\"" + username + "\",\"" + like_word + "\",0,0,0;");
    }
    if (source == 1) { //from bing
      stmt.execute("update user_like set bing=1 where word=\"" + like_word + "\";");
    }
    else if (source == 2) { //from neteast
      stmt.execute("update user_like set neteast=1 where word=\"" + like_word + "\";");
    }
    else if (source == 3) { //from baidu
      stmt.execute("update user_like set baidu=1 where word=\"" + like_word + "\";");
    }

    // 6.处理从数据库中获取到的数据

    // 7.关闭链接,释放资源
    rs.close();
    stmt.close();
    conn.close();
  }
}