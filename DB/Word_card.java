import java.sql.*;


public class Word_card 
{
	 private final static String DBDRIVER = "org.gjt.mm.mysql.Driver";

	  private final static String database = "DICT";

	  private final static String url = "jdbc:mysql://localhost:3306/" + database;

	  private String user = null;

	  private String control_password = null;

	  public Word_card(String user, String password) 
	  {
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
	 public boolean new_word_card(String username, String details,String sender) 
	 {
		    boolean result = false;
		    String sql = "select * from word_card where username=\"" + username + "\" and details=\""+details+"\" and sender=\""+sender+"\" ;";
		    try (
		        Connection conn = DriverManager.getConnection(url, user, control_password);
		        Statement stmt = conn.createStatement();
		        ResultSet rs = stmt.executeQuery(sql)
		    ) {
		      if (rs.next()) {
		        System.out.println("User " + username + " already exists the card from this sender.");
		      }
		      else {
		        stmt.executeUpdate(String.format(
		            "insert into word_card (username, details, sender) values ('%s', '%s', '%s');",
		            username, details,sender
		        ));
		        rs.close();  
			    stmt.close();  
			    conn.close(); 
		        result = true;
		      }
		    }
		    catch (SQLException e) {
		      System.err.println("Failed to execute register query for " + username);
		      System.err.println(e.toString());
		    }
		    return result;
		  }
	 public ResultSet search_word_card(String username, String details,String sender) throws SQLException 
	 {
		 String sql = "select * from word_card where username=\"" + username +"\";";
	     Connection conn = DriverManager.getConnection(url, user, control_password);
	     Statement stmt = conn.createStatement();
	     ResultSet rs = stmt.executeQuery(sql);
	     if(rs.next())
	     {
	    	 return rs;//有结果返回结果
	     }
	     else
	     {
	    	 return null;//没结果有的时候mysql会返回奇怪的东西所以这里手动返回null
	     }
	 }					    
 }

