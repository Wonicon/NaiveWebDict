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
	    // 4.��ȡStatement����  
	    Statement stmt = conn.createStatement();  
	    // 5.ִ��SQL���  
	    stmt.executeQuery("use naive2");
	    String sql = "select password from user where username=\""+username+"\";";  
	    ResultSet rs = stmt.executeQuery(sql);  
	    // 6.��������ݿ��л�ȡ��������  	
	    if(!rs.next())
	    {
	    	System.out.println("������û�������ĳ���");
		    rs.close();  
		    stmt.close();  
		    conn.close(); 
	    	return false;//��½ʧ��
	    }
	    else
	    {
	    	String test=rs.getString("password");
	    	if(test.equals(password))
	    	{
	    		stmt.execute("update user set login=1 where username=\""+username+"\";");
	    		System.out.println("�ɹ�������+1s��");
	    	    rs.close();  
	    	    stmt.close();  
	    	    conn.close(); 
	    		return true;//��½�ɹ�
	    	}
	    	else
	    	{
	    		System.out.println(test);  
	    		System.out.println("���Ĥ�����Ʋ���");
	    	    rs.close();  
	    	    stmt.close();  
	    	    conn.close(); 
	    		return false;//��½ʧ��
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
	    // 4.��ȡStatement����  
	    Statement stmt = conn.createStatement();  
	    // 5.ִ��SQL���  
	    stmt.executeQuery("use naive2");
		stmt.execute("update user set login=0 where username=\""+username+"\";");//�����Ѿ�ȷ�������������Բ����ǻ�õ����������⡣
	    // 6.��������ݿ��л�ȡ��������  	
	    // 7.�ر�����,�ͷ���Դ   
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
	    // 4.��ȡStatement����  
	    Statement stmt = conn.createStatement();  
	    // 5.ִ��SQL���  
	    stmt.executeQuery("use naive2");
	    String sql = "select password from user where username=\""+username+"\";";  
	    ResultSet rs = stmt.executeQuery(sql);  
	    // 6.��������ݿ��л�ȡ��������  	
	    if(rs.next())
	    {
	    	System.out.println("������Ҳ�뵱���ߣ��Ҹ��������������������һ��������û��ȥ�����Һ�����̸Ц����");
		    rs.close();  
		    stmt.close();  
		    conn.close(); 
		    return false;//ע��ʧ�ܣ�ʧ��ԭ���û����ظ�
	    }
	    else
	    {
	    	stmt.execute("insert user values(\""+username+"\",\""+password+"\",1);"); 
	    	System.out.println("wow,һ���µĳ��ߣ�");
	    // 7.�ر�����,�ͷ���Դ  
	    rs.close();  
	    stmt.close();  
	    conn.close();  
	    return true;//ע���ɹ������Ĭ��ֱ���Ѿ���¼��ע��
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
	    // 4.��ȡStatement����  
	    Statement stmt = conn.createStatement();  
	    // 5.ִ��SQL���  
	    stmt.executeQuery("use naive2");
	    ResultSet rs = stmt.executeQuery("select * from user_like where username=\""+username+"\" and word=\""+like_word+"\" and dict_id="+source+";");
		if(!rs.next())//�ж��Ƿ����
		{
	    	stmt.execute("insert user_like values(\"" +username+ "\",\"" + like_word + "\"," + source + ";"); 
	    	rs=stmt.executeQuery("select * from count where word=\""+like_word+"\" and source="+source+";");
	    	if(!rs.next())//�������û���˵�������µ�count
	    	{
		    	stmt.execute("insert count values(\"" +like_word+ "\"," + source + ",1;"); 
	    	}
	    	else//�Ѿ����������update
	    	{
		    	stmt.execute("update count set count=count+1 where word=\""+like_word+"\" and dict_id="+source+";"); 
	    	}
	    	rs.close();
		    stmt.close();  
		    conn.close(); 
	    	return true;//�ɹ�����
		}
		else
		{
	    rs.close();
	    stmt.close();  
	    conn.close();  
	    return false;//����ʧ�ܣ���λѡ���Ѿ��������
		}
	}  
	//���µĺ�����û����ȫ���
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
	    // 4.��ȡStatement����  
	    Statement stmt = conn.createStatement();  
	    // 5.ִ��SQL���  
	    stmt.executeQuery("use naive2");
	    //������������ģ�ֻ�����˴���û�й��ĵ��޵���
	    ResultSet rs = stmt.executeQuery("select * from user_like where username=\""+username+"\" and word=\""+like_word+"\" and dict_id="+source+";");
		if(!rs.next())//�ж��Ƿ����
		{
	    	rs.close();
		    stmt.close();  
		    conn.close(); 
	    	return false;//ȡ������ʧ�ܣ�û�������ôȡ��
		}
		else
		{
			stmt.execute("delete from user_like where username=\""+username+"\" and word=\""+like_word+"\" and dict_id="+source+";"); 
	    	stmt.execute("update count set count=count-1 where word=\""+like_word+"\" and dict_id="+source+";"); 
	    rs.close();
	    stmt.close();  
	    conn.close();  
	    return false;//ȡ���ɹ�
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
	    // 4.��ȡStatement����  
	    Statement stmt = conn.createStatement();  
	    // 5.ִ��SQL���  
	    stmt.executeQuery("use naive2");
	    ResultSet rs = stmt.executeQuery("select dict_id,count from count where word=\""+like_word+"\" order by dict_id; ");
	    return rs;//����ķ��صĽ�����һ����dict_id,count������ɵ�һ����������dict_id�������һ��������ϸ���ܵ�״������˵���ļ��н���
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
	    // 4.��ȡStatement����  
	    Statement stmt = conn.createStatement();  
	    // 5.ִ��SQL���  
	    stmt.executeQuery("use naive2");
	    boolean rs = stmt.execute("select * from user_like where username=\""+username+"\" and word=\""+like_word+"\" and dict_id="+source+";");
	    return rs;
	}

}

