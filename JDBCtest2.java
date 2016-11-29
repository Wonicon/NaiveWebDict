import java.sql.*;  

public class JDBCtest2 
{    
	final static String DBDRIVER = "org.gjt.mm.mysql.Driver" ;
	final static String url = "jdbc:mysql://localhost:3306/test";  
	final static String user = "root";  
	final static String control_password = "onts0624";  
	public static void main(String args[])throws ClassNotFoundException,SQLException
	{
		try
		{
			Class.forName(DBDRIVER) ;
		}
		catch(ClassNotFoundException e)
		{
			e.printStackTrace() ;
		}
	    Connection conn = DriverManager.getConnection(url, user, control_password);  
	    // 4.获取Statement对象  
	    Statement stmt = conn.createStatement();  
	    // 5.执行SQL语句  
	    stmt.executeQuery("use naive");
	    String x="onts";
	    String sql = "select password from user where username=\""+x+"\"";  
	    ResultSet rs = stmt.executeQuery(sql);  
	    // 6.处理从数据库中获取到的数据  
    	
	    if(!rs.next())
	    {
	    	System.out.println("根本就没有这样的长者");
	    }
	    else
	    {
	    	String test=rs.getString("password");
	    	if(test.equals("onts0624"))
	    	{
	    		stmt.execute("update user set login=1 where username=\"onts\";");
	    		System.out.println("成功续命（+1s）");
	    	}
	    	else
	    	{
	    		System.out.println(test);  
	    		System.out.println("你的膜蛤姿势不对");
	    	}
	    	/*System.out.println( rs.getString("password"));  
	    while (rs.next()) 
	    {  
	    	System.out.println( rs.getString("password"));  
	    	//System.out.println(rs.getInt(1) + "\t" + rs.getString("password"));  
	    } */
	    }
	    // 7.关闭链接,释放资源  
	    rs.close();  
	    stmt.close();  
	    conn.close();  
	}  
	
	public void login(String username,String password)throws ClassNotFoundException,SQLException
	{
		try
		{
			Class.forName(DBDRIVER) ;
		}
		catch(ClassNotFoundException e)
		{
			e.printStackTrace() ;
		}
	    Connection conn = DriverManager.getConnection(url,user,control_password);  
	    // 4.获取Statement对象  
	    Statement stmt = conn.createStatement();  
	    // 5.执行SQL语句  
	    stmt.executeQuery("use naive");
	    String sql = "select password from user where username=\""+username+"\";";  
	    ResultSet rs = stmt.executeQuery(sql);  
	    // 6.处理从数据库中获取到的数据  	
	    if(!rs.next())
	    {
	    	System.out.println("根本就没有这样的长者");
	    }
	    else
	    {
	    	String test=rs.getString("password");
	    	if(test.equals(password))
	    	{
	    		stmt.execute("update user set login=1 where username=\""+username+"\";");
	    		System.out.println("成功续命（+1s）");
	    	}
	    	else
	    	{
	    		System.out.println(test);  
	    		System.out.println("你的膜蛤姿势不对");
	    	}
	    	/*System.out.println( rs.getString("password"));  
	    while (rs.next()) 
	    {  
	    	System.out.println( rs.getString("password"));  
	    	//System.out.println(rs.getInt(1) + "\t" + rs.getString("password"));  
	    } */
	    }
	    // 7.关闭链接,释放资源  
	    rs.close();  
	    stmt.close();  
	    conn.close();  
	}  
	public void logout(String username,String password)throws ClassNotFoundException,SQLException
	{	
		try
		{
			Class.forName(DBDRIVER) ;
		}
		catch(ClassNotFoundException e)
		{
			e.printStackTrace() ;
		}
	    Connection conn = DriverManager.getConnection(url,user,control_password);  
	    // 4.获取Statement对象  
	    Statement stmt = conn.createStatement();  
	    // 5.执行SQL语句  
	    stmt.executeQuery("use naive");
		stmt.execute("update user set login=0 where username=\""+username+"\";");//由于已经确定有数据了所以不考虑获得的数据有问题。
	    // 6.处理从数据库中获取到的数据  	
	    
	    // 7.关闭链接,释放资源   
	    stmt.close();  
	    conn.close();  
	}  
	public void register(String username,String password)throws ClassNotFoundException,SQLException
	{
		try
		{
			Class.forName(DBDRIVER) ;
		}
		catch(ClassNotFoundException e)
		{
			e.printStackTrace() ;
		}
	    Connection conn = DriverManager.getConnection(url,user,control_password);  
	    // 4.获取Statement对象  
	    Statement stmt = conn.createStatement();  
	    // 5.执行SQL语句  
	    stmt.executeQuery("use naive");
	    String sql = "select password from user where username=\""+username+"\";";  
	    ResultSet rs = stmt.executeQuery(sql);  
	    // 6.处理从数据库中获取到的数据  	
	    if(rs.next())
	    {
	    	System.out.println("你这样也想当长者？我告诉你早就有啦，西方哪一个国家我没有去过，我和他们谈笑风生");
	    }
	    else
	    {
	    	stmt.executeQuery("insert user values(0,\""+username+"\",\""+password+"\",1);"); 
	    	System.out.println("wow,一个新的长者！");

	    	/*System.out.println( rs.getString("password"));  
	    while (rs.next()) 
	    {  
	    	System.out.println( rs.getString("password"));  
	    	//System.out.println(rs.getInt(1) + "\t" + rs.getString("password"));  
	    } */
	    }
	    // 7.关闭链接,释放资源  
	    rs.close();  
	    stmt.close();  
	    conn.close();  
	}  
	//以下的函数还没有完全完成
	public void like_word(String username,String like_word,int source)throws ClassNotFoundException,SQLException
	{	
		try
		{
			Class.forName(DBDRIVER) ;
		}
		catch(ClassNotFoundException e)
		{
			e.printStackTrace() ;
		}
	    Connection conn = DriverManager.getConnection(url,user,control_password);  
	    // 4.获取Statement对象  
	    Statement stmt = conn.createStatement();  
	    // 5.执行SQL语句  
	    stmt.executeQuery("use naive");
	    //这里是有问题的，只关心了词语没有关心点赞的人
	    ResultSet rs = stmt.executeQuery("select * from user_like where word=\""+like_word+"\";");
		if(!rs.next())//数据库中只保存被点赞过的词，如果没有被点赞过先生成
		{
	    	stmt.executeQuery("insert user_like values(\"" +username+ "\",\""+like_word+"\",0,0,0;"); 
		}
		if(source==1)//from bing
		{
			stmt.execute("update user_like set bing=1 where word=\""+like_word+"\";");
		}
		else if(source==2)//from neteast
		{
			stmt.execute("update user_like set neteast=1 where word=\""+like_word+"\";");
		}
		else if(source==3)//from baidu
		{
			stmt.execute("update user_like set baidu=1 where word=\""+like_word+"\";");
		}
	    // 6.处理从数据库中获取到的数据  	
	    
	    // 7.关闭链接,释放资源   
	    rs.close();
	    stmt.close();  
	    conn.close();  
	}  
	public void serach_like_word(String username,String like_word,int source)throws ClassNotFoundException,SQLException
	{	
		try
		{
			Class.forName(DBDRIVER) ;
		}
		catch(ClassNotFoundException e)
		{
			e.printStackTrace() ;
		}
	    Connection conn = DriverManager.getConnection(url,user,control_password);  
	    // 4.获取Statement对象  
	    Statement stmt = conn.createStatement();  
	    // 5.执行SQL语句  
	    stmt.executeQuery("use naive");
	    ResultSet rs = stmt.executeQuery("select * from user_like where word=\""+like_word+"\";");
		if(!rs.next())//数据库中只保存被点赞过的词，如果没有被点赞过先生成
		{
	    	stmt.executeQuery("insert user_like values(\"" +username+ "\",\""+like_word+"\",0,0,0;"); 
		}
		if(source==1)//from bing
		{
			stmt.execute("update user_like set bing=1 where word=\""+like_word+"\";");
		}
		else if(source==2)//from neteast
		{
			stmt.execute("update user_like set neteast=1 where word=\""+like_word+"\";");
		}
		else if(source==3)//from baidu
		{
			stmt.execute("update user_like set baidu=1 where word=\""+like_word+"\";");
		}
	    // 6.处理从数据库中获取到的数据  	
	    
	    // 7.关闭链接,释放资源   
	    rs.close();
	    stmt.close();  
	    conn.close();  
	}  

}

