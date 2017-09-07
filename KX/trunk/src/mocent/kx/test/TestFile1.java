package mocent.kx.test;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import org.apache.commons.io.IOUtils;
import org.apache.commons.net.ftp.FTPClient;

public class TestFile1 {

	public static void main(String[] args) {
		  FTPClient ftpClient = new FTPClient();  
	        FileInputStream fis = null; 
	        try {  
	            ftpClient.connect("120.76.250.32");  
	            ftpClient.login("root", "zongheng2013Mocent");  
	  
	            File srcFile = new File("E:/image/2016/11/30/1480493256164.jpg");  
	            fis = new FileInputStream(srcFile);  
	            // 设置上传目录  
	            ftpClient.changeWorkingDirectory("/var/download/mocentKX/2016/11/29");  
	            ftpClient.setBufferSize(1024);  
	            ftpClient.setControlEncoding("GBK");  
	            // 设置文件类型（二进制）  
	            ftpClient.setFileType(FTPClient.BINARY_FILE_TYPE);  
	            ftpClient.storeFile("1480493256164.jpg", fis);  
	        } catch (IOException e) {  
	            e.printStackTrace();  
	            throw new RuntimeException("FTP客户端出错！", e);  
	        } finally {  
	            IOUtils.closeQuietly(fis);  
	            try {  
	                ftpClient.disconnect();  
	            } catch (IOException e) {  
	                e.printStackTrace();  
	                throw new RuntimeException("关闭FTP连接发生异常！", e);  
	            }  
	        } 
		
		/*try {
			boolean a=FtpUtils.connectServer("120.76.250.32", "22", "root", "zongheng2013Mocent", "UTF-8", false);
			System.out.println(a);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
	}
	
	
}
