package mocent.kx.util;

import java.io.InputStream;
import java.util.Properties;

/**
 * 系统相关配置
 * @author Administrator
 *
 */
public class ConfigUtil {
	
	private static final String fileName = "/config.properties";
	
	public static String uploadUrl = "";
	public static String newLine="";
	public static String cmdSuff="";
	public static String iptcPath="";
	
	public static String cardics_svr = "";
	
	public static String username = "";
	
	public static String password = "";
	public static String ftphost="";
	
	public static String ftpport="";
	
	public static String ftpuser="";
	
	public static String ftppassword="";
	
	public static String ftpcharset="";
	
	public static String mocentUUM="";
	
	public static void initlize() { 
         Properties prop = new Properties();     
         try{
             //读取属性文件config.properties
        	 InputStream in = ConfigUtil.class.getResourceAsStream(fileName);
        	 prop.load(in);
 		     in.close();
 		     if(prop.containsKey("uploadUrl")){  
 		    	uploadUrl = prop.getProperty("uploadUrl");  
 		     } 
 		    if(prop.containsKey("newLine")){  
 		    	newLine = prop.getProperty("newLine");  
 		     }
 		   if(prop.containsKey("cmdSuff")){  
 			  cmdSuff = prop.getProperty("cmdSuff");  
		     }
 		  if(prop.containsKey("iptcPath")){  
 			 iptcPath = prop.getProperty("iptcPath");  
		     }
 		 if(prop.containsKey("cardics_svr")){  
		    	cardics_svr = prop.getProperty("cardics_svr");  
		     }  
		     if(prop.containsKey("username")){  
		    	username = prop.getProperty("username");  
		     }
		     if(prop.containsKey("password")){
		    	password =prop.getProperty("password");
		     }
 		  if(prop.containsKey("ftphost")){
		    	ftphost = prop.getProperty("ftphost");
		     }
		    if(prop.containsKey("ftpport")){
		    	ftpport = prop.getProperty("ftpport");
		     }
		    if(prop.containsKey("ftpuser")){
		    	ftpuser = prop.getProperty("ftpuser");
		     }
		    if(prop.containsKey("ftppassword")){
		    	ftppassword = prop.getProperty("ftppassword");
		     }
		    if(prop.containsKey("ftpcharset")){
		    	ftpcharset = prop.getProperty("ftpcharset");
		    }
		    if(prop.containsKey("mocentUUM")){
		    	mocentUUM = prop.getProperty("mocentUUM");
		    }
		    
         }
         catch(Exception e){
             System.out.println(e);
         }
     } 
}
