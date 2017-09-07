package mocent.Monitor.Config;

import java.io.InputStream;
import java.util.Properties;

/**
 * 系统相关配置
 * @author Administrator
 *
 */
public class ConfigUtil {
	
	private static final String fileName = "/config.properties";
	
	public static String Server_Host_Ip = "";
	
	public static int Server_Host_Port = 0;
	
	public static int managerId = -1;
	
	public static void initlize() { 
         Properties prop = new Properties();     
         try{
             //读取属性文件config.properties
        	 InputStream in = ConfigUtil.class.getResourceAsStream(fileName);//这里有人用new FileInputStream(fileName),不过这种方式找不到配置文件。有人说是在classes下，我调过了，不行。
        	 prop.load(in);
 		     in.close();
 		     if(prop.containsKey("hostIp")){  
                Server_Host_Ip = prop.getProperty("hostIp");  
 		     }  
 		     if(prop.containsKey("hostPort")){  
 		    	Server_Host_Port = Integer.valueOf(prop.getProperty("hostPort"));  
 		     }
 		     if(prop.containsKey("managerId")){
 		    	 managerId = Integer.valueOf(prop.getProperty("managerId"));
 		     }
         }
         catch(Exception e){
             System.out.println(e);
         }
     } 
}
