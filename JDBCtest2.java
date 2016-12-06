import java.sql.*;  

public class JDBCtest2 
{    
	final static String DBDRIVER = "org.gjt.mm.mysql.Driver" ;
	final static String url = "jdbc:mysql://localhost:3306/test";  
	final static String user = "root";  
	final static String control_password = "onts0624";  	
	public boolean login(String username,String password)throws ClassNotFoundException,SQLException
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
	    stmt.executeQuery("use naive2");
	    String sql = "select password from user where username=\""+username+"\";";  
	    ResultSet rs = stmt.executeQuery(sql);  
	    // 6.处理从数据库中获取到的数据  	
	    if(!rs.next())
	    {
	    	System.out.println("根本就没有这样的长者");
		    rs.close();  
		    stmt.close();  
		    conn.close(); 
	    	return false;//登陆失败
	    }
	    else
	    {
	    	String test=rs.getString("password");
	    	if(test.equals(password))
	    	{
	    		stmt.execute("update user set login=1 where username=\""+username+"\";");
	    		System.out.println("成功续命（+1s）");
	    	    rs.close();  
	    	    stmt.close();  
	    	    conn.close(); 
	    		return true;//登陆成功
	    	}
	    	else
	    	{
	    		System.out.println(test);  
	    		System.out.println("你的膜蛤姿势不对");
	    	    rs.close();  
	    	    stmt.close();  
	    	    conn.close(); 
	    		return false;//登陆失败
	    	}
	    }  
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
	    stmt.executeQuery("use naive2");
		stmt.execute("update user set login=0 where username=\""+username+"\";");//由于已经确定有数据了所以不考虑获得的数据有问题。
	    // 6.处理从数据库中获取到的数据  	
	    // 7.关闭链接,释放资源   
	    stmt.close();  
	    conn.close();  
	}  
	public boolean register(String username,String password)throws ClassNotFoundException,SQLException
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
	    stmt.executeQuery("use naive2");
	    String sql = "select password from user where username=\""+username+"\";";  
	    ResultSet rs = stmt.executeQuery(sql);  
	    // 6.处理从数据库中获取到的数据  	
	    if(rs.next())
	    {
	    	System.out.println("你这样也想当长者？我告诉你早就有啦，西方哪一个国家我没有去过，我和他们谈笑风生");
		    rs.close();  
		    stmt.close();  
		    conn.close(); 
		    return false;//注册失败，失败原因：用户名重复
	    }
	    else
	    {
	    	stmt.execute("insert user values(\""+username+"\",\""+password+"\",1);"); 
	    	System.out.println("wow,一个新的长者！");
	    // 7.关闭链接,释放资源  
	    rs.close();  
	    stmt.close();  
	    conn.close();  
	    return true;//注册大成功，这边默认直接已经登录请注意
	    }
	}  
	public boolean like_word(String username,String like_word,int source)throws ClassNotFoundException,SQLException
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
	    stmt.executeQuery("use naive2");
	    ResultSet rs = stmt.executeQuery("select * from user_like where username=\""+username+"\" and word=\""+like_word+"\" and dict_id="+source+";");
		if(!rs.next())//判断是否点赞
		{
	    	stmt.execute("insert user_like values(\"" +username+ "\",\"" + like_word + "\"," + source + ";"); 
	    	rs=stmt.executeQuery("select * from count where word=\""+like_word+"\" and source="+source+";");
	    	if(!rs.next())//如果从来没有人点过生成新的count
	    	{
		    	stmt.execute("insert count values(\"" +like_word+ "\"," + source + ",1;"); 
	    	}
	    	else//已经存在则进行update
	    	{
		    	stmt.execute("update count set count=count+1 where word=\""+like_word+"\" and dict_id="+source+";"); 
	    	}
	    	rs.close();
		    stmt.close();  
		    conn.close(); 
	    	return true;//成功点赞
		}
		else
		{
	    rs.close();
	    stmt.close();  
	    conn.close();  
	    return false;//点赞失败（这位选手已经点过赞了
		}
	}  
	//以下的函数还没有完全完成
	public boolean dislike_word(String username,String like_word,int source)throws ClassNotFoundException,SQLException
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
	    stmt.executeQuery("use naive2");
	    //这里是有问题的，只关心了词语没有关心点赞的人
	    ResultSet rs = stmt.executeQuery("select * from user_like where username=\""+username+"\" and word=\""+like_word+"\" and dict_id="+source+";");
		if(!rs.next())//判断是否点赞
		{
	    	rs.close();
		    stmt.close();  
		    conn.close(); 
	    	return false;//取消点赞失败（没点过赞怎么取消
		}
		else
		{
			stmt.execute("delete from user_like where username=\""+username+"\" and word=\""+like_word+"\" and dict_id="+source+";"); 
	    	stmt.execute("update count set count=count-1 where word=\""+like_word+"\" and dict_id="+source+";"); 
	    rs.close();
	    stmt.close();  
	    conn.close();  
	    return false;//取消成功
		}  
	}
	public ResultSet query_count(String like_word)throws ClassNotFoundException,SQLException
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
	    stmt.executeQuery("use naive2");
	    ResultSet rs = stmt.executeQuery("select dict_id,count from count where word=\""+like_word+"\" order by dict_id; ");
	    return rs;//这里的返回的将会是一个由dict_id,count三列组成的一个表，并且以dict_id排序进行一个升序，详细可能的状况会在说明文件中解释
	}
	public boolean query_user(String like_word,int source,String username)throws ClassNotFoundException,SQLException
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
	    stmt.executeQuery("use naive2");
	    boolean rs = stmt.execute("select * from user_like where username=\""+username+"\" and word=\""+like_word+"\" and dict_id="+source+";");
	    return rs;
	}

}

