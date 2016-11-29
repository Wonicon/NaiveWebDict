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
	    // 4.��ȡStatement����  
	    Statement stmt = conn.createStatement();  
	    // 5.ִ��SQL���  
	    stmt.executeQuery("use naive");
	    String x="onts";
	    String sql = "select password from user where username=\""+x+"\"";  
	    ResultSet rs = stmt.executeQuery(sql);  
	    // 6.��������ݿ��л�ȡ��������  
    	
	    if(!rs.next())
	    {
	    	System.out.println("������û�������ĳ���");
	    }
	    else
	    {
	    	String test=rs.getString("password");
	    	if(test.equals("onts0624"))
	    	{
	    		stmt.execute("update user set login=1 where username=\"onts\";");
	    		System.out.println("�ɹ�������+1s��");
	    	}
	    	else
	    	{
	    		System.out.println(test);  
	    		System.out.println("���Ĥ�����Ʋ���");
	    	}
	    	/*System.out.println( rs.getString("password"));  
	    while (rs.next()) 
	    {  
	    	System.out.println( rs.getString("password"));  
	    	//System.out.println(rs.getInt(1) + "\t" + rs.getString("password"));  
	    } */
	    }
	    // 7.�ر�����,�ͷ���Դ  
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
	    // 4.��ȡStatement����  
	    Statement stmt = conn.createStatement();  
	    // 5.ִ��SQL���  
	    stmt.executeQuery("use naive");
	    String sql = "select password from user where username=\""+username+"\";";  
	    ResultSet rs = stmt.executeQuery(sql);  
	    // 6.��������ݿ��л�ȡ��������  	
	    if(!rs.next())
	    {
	    	System.out.println("������û�������ĳ���");
	    }
	    else
	    {
	    	String test=rs.getString("password");
	    	if(test.equals(password))
	    	{
	    		stmt.execute("update user set login=1 where username=\""+username+"\";");
	    		System.out.println("�ɹ�������+1s��");
	    	}
	    	else
	    	{
	    		System.out.println(test);  
	    		System.out.println("���Ĥ�����Ʋ���");
	    	}
	    	/*System.out.println( rs.getString("password"));  
	    while (rs.next()) 
	    {  
	    	System.out.println( rs.getString("password"));  
	    	//System.out.println(rs.getInt(1) + "\t" + rs.getString("password"));  
	    } */
	    }
	    // 7.�ر�����,�ͷ���Դ  
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
	    // 4.��ȡStatement����  
	    Statement stmt = conn.createStatement();  
	    // 5.ִ��SQL���  
	    stmt.executeQuery("use naive");
		stmt.execute("update user set login=0 where username=\""+username+"\";");//�����Ѿ�ȷ�������������Բ����ǻ�õ����������⡣
	    // 6.��������ݿ��л�ȡ��������  	
	    
	    // 7.�ر�����,�ͷ���Դ   
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
	    // 4.��ȡStatement����  
	    Statement stmt = conn.createStatement();  
	    // 5.ִ��SQL���  
	    stmt.executeQuery("use naive");
	    String sql = "select password from user where username=\""+username+"\";";  
	    ResultSet rs = stmt.executeQuery(sql);  
	    // 6.��������ݿ��л�ȡ��������  	
	    if(rs.next())
	    {
	    	System.out.println("������Ҳ�뵱���ߣ��Ҹ��������������������һ��������û��ȥ�����Һ�����̸Ц����");
	    }
	    else
	    {
	    	stmt.executeQuery("insert user values(0,\""+username+"\",\""+password+"\",1);"); 
	    	System.out.println("wow,һ���µĳ��ߣ�");

	    	/*System.out.println( rs.getString("password"));  
	    while (rs.next()) 
	    {  
	    	System.out.println( rs.getString("password"));  
	    	//System.out.println(rs.getInt(1) + "\t" + rs.getString("password"));  
	    } */
	    }
	    // 7.�ر�����,�ͷ���Դ  
	    rs.close();  
	    stmt.close();  
	    conn.close();  
	}  
	//���µĺ�����û����ȫ���
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
	    // 4.��ȡStatement����  
	    Statement stmt = conn.createStatement();  
	    // 5.ִ��SQL���  
	    stmt.executeQuery("use naive");
	    //������������ģ�ֻ�����˴���û�й��ĵ��޵���
	    ResultSet rs = stmt.executeQuery("select * from user_like where word=\""+like_word+"\";");
		if(!rs.next())//���ݿ���ֻ���汻���޹��Ĵʣ����û�б����޹�������
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
	    // 6.��������ݿ��л�ȡ��������  	
	    
	    // 7.�ر�����,�ͷ���Դ   
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
	    // 4.��ȡStatement����  
	    Statement stmt = conn.createStatement();  
	    // 5.ִ��SQL���  
	    stmt.executeQuery("use naive");
	    ResultSet rs = stmt.executeQuery("select * from user_like where word=\""+like_word+"\";");
		if(!rs.next())//���ݿ���ֻ���汻���޹��Ĵʣ����û�б����޹�������
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
	    // 6.��������ݿ��л�ȡ��������  	
	    
	    // 7.�ر�����,�ͷ���Դ   
	    rs.close();
	    stmt.close();  
	    conn.close();  
	}  

}

