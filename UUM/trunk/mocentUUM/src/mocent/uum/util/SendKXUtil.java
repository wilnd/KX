package mocent.uum.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import mocent.uum.util.ConfigUtil;

import org.apache.log4j.Logger;

public class SendKXUtil {

	private static final Logger log = Logger.getLogger(SendKXUtil.class);
	
	/*public static final String url = "jdbc:mysql://120.76.250.32/cardics_svr?useUnicode=true&characterEncoding=UTF-8&autoReconnect=true&allowMultiQueries=true" ;    
	public static final  String username = "root";   
	public static final String password = "mocent123456"; */
	
	public static Connection connet(){
		if("".equals(ConfigUtil.cardics_svr)){
			ConfigUtil.initlize();
		}
		 Connection conn = null;  
		 try {
			Class.forName("com.mysql.jdbc.Driver");
			conn =DriverManager.getConnection(ConfigUtil.cardics_svr , ConfigUtil.username , ConfigUtil.password ); 
		} catch (ClassNotFoundException e) {
			log.error("找不到驱动程序类 ，加载驱动失败！");
			e.printStackTrace();
		} catch (Exception e) {
			log.error(e.toString());
			e.printStackTrace();
		}
		 return conn;
	}
	
	public static void closeConn(Connection conn){
		try {
			if(null !=conn) conn.close();
		} catch (SQLException e) {
			log.error("关闭连接失败");
			e.printStackTrace();
		}
	}
}
