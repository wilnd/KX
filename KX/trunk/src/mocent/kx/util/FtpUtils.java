package mocent.kx.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.SocketException;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;
import org.apache.log4j.Logger;

public class FtpUtils {
	private static FTPClient ftp;
	private static final Logger log = Logger.getLogger(FtpUtils.class);
	
	 public static boolean connectServer(String host, String port, String user, String password, String charset, boolean isTextMode)
	    {
	        log.info("开始连接FTP【" + host + ":" + port + "】...");
	        boolean isLogin = false;
	        ftp = new FTPClient();
	       
	        // 连接FTP
	        try
	        {
	            ftp.connect(host, Integer.parseInt(port));
	         //  ftp.connect(host);
	        }
	        catch (SocketException e)
	        {
	            log.error("连接FTP服务器【" + host + ":" + port + "】失败，" + e.getMessage());
	            return isLogin;
	        }
	        catch (IOException e)
	        {
	            log.error("连接FTP服务器异常：" + e.getMessage());
	            return isLogin;
	        }
	        // 连接时设置编码，否则乱码
	        ftp.setControlEncoding(charset);
	        // 登录FTP
	        try
	        {
	            if(!ftp.login(user, password))
	            {
	                log.error("登录FTP服务器【" + host + ":" + port + "】失败");
	                return isLogin;
	            }
	            ftp.setFileTransferMode(FTP.STREAM_TRANSFER_MODE);
	        }
	        catch (IOException e)
	        {
	            log.error("登录FTP服务器异常：" + e.getMessage());
	            return isLogin;
	        }
	       
	        // 设置连接超时时间
	        try
	        {
	            ftp.setSoTimeout(60*1000);
	        }
	        catch (SocketException e)
	        {
	            log.error("设置连接超时时间异常：" + e.getMessage());
	        }  
	       
	        // 设置传输超时时间
	        ftp.setDataTimeout(60*1000);
	       
	        try
	        {
	            // 设置文件传输类型
	            if(isTextMode)
	            {
	                ftp.setFileType(FTPClient.BINARY_FILE_TYPE); // 设置文件类型（二进制）
	            }
	            else
	            {
	                ftp.setFileType(FTPClient.ASCII_FILE_TYPE); // 设置文件类型（ASCII）
	            }
	            //FTPClientConfig conf = new FTPClientConfig(FTPClientConfig.SYST_UNIX);
	            //conf.setServerLanguageCode("zh");
	        }
	        catch (IOException e)
	        {
	            log.error("设置文件传输类型异常：" + e.getMessage());
	        }
	       
	        // 判断是否登陆成功
	        if(!FTPReply.isPositiveCompletion(ftp.getReplyCode()))
	        {
	            log.error("登录FTP服务器【" + host + ":" + port + "】失败,状态码【"+ ftp.getReplyCode() + "】");
	        }
	        else
	        {
	            isLogin = true;
	            log.info("连接FTP【" + host + ":" + port + "】成功");
	        }
	       
	        // 设置文件传输模式:被动模式
	       ftp.enterLocalPassiveMode();
	       
	        return isLogin;
	    }
	 public static boolean uploadStep(String remotePath, String fileName, String localFile, String charset)
	    {
	        boolean isOK = false;
	        log.info("上传文件名：" + fileName);
	        log.info("上传FTP服务器路径：" + remotePath);
	       
	        File file = new File(localFile+"/"+fileName);
	        // 判断本地文件是否存在
	        if(!file.exists())
	        {
	            isOK = false;
	            log.error("本地上传文件" + localFile + "不存在");
	        }
	        else
	        {
	            FileInputStream in = null;
	            try
	            {
	                in = new FileInputStream(file);
	                log.info("本地上传文件：" + localFile + "，大小" + in.available()/1024 + "KB");
	            }
	            catch (Exception e)
	            {
	                log.error("本地上传文件" + localFile + "读取异常：" + e.getMessage());
	            }
	           
	            try
	            {
	                if(null != in)
	                {
	                    // 转移到FTP服务器目录
	                    /*if(!ftp.changeWorkingDirectory(remotePath)){
	                     System.out.println(ftp.makeDirectory(remotePath));  
	                    };*/
	                    if(ftp.storeFile(new String(fileName.getBytes(charset), "iso-8859-1"), in))
	                    {
	                        isOK = true;
	                        log.info("上传文件" + fileName + "成功");
	                    }
	                    else
	                    {
	                        isOK = false;
	                        log.info("上传文件" + fileName + "失败");
	                    }
	                }
	            }
	            catch (IOException e)
	            {
	                isOK = false;
	                log.error(fileName +"上传FTP服务器异常：" + e.getMessage());
	                try {
						ftp.deleteFile(fileName);
					} catch (IOException e1) {
						log.error("删除文件失败");
						e1.printStackTrace();
					}
	            }
	            try
	            {
	                if (null != in)
	                {
	                    in.close();
	                }
	            }
	            catch (IOException e)
	            {
	                log.error(fileName +"上传FTP成功，关闭输入流异常：" + e.getMessage());
	            }
	           
	        }
	       
	        return isOK;
	       
	    }
	 public static boolean downloadStep(String remotePath, String ftpFileName, String localPath, String localFileName, String charset)
	    {
	     
	        boolean isOK = false;
	        if(null != localFileName && localFileName.isEmpty())
	        {
	            localFileName = ftpFileName;
	        }
	        log.info("下载文件名：" + ftpFileName);
	        log.info("下载FTP服务器路径：" + remotePath);
	        log.info("文件保存本地路径：" + localPath);
	        log.info("文件保存本地文件名：" + localFileName);
	        // 判断FTP服务器文件路径下是否存在该文件
	            // 设置下载缓冲
	            ftp.setBufferSize(102400);
	            File file=new File(localPath);
	            if(!file.exists()){
	            	file.mkdirs();
	            }
	            
	            File localFile = new File(localPath +"/"+ localFileName);
	            OutputStream out = null;
	            try
	            {
	             
	                out = new FileOutputStream(localFile);
	                if(ftp.retrieveFile(new String(ftpFileName.getBytes(charset), "UTF-8"), out))
	                {
	                	System.out.println("xiazai****************");
	                    isOK = true;
	                    log.info("下载FTP文件" + ftpFileName + "成功，保存至本地的文件名为" + localFileName);
	                }
	                else
	                {
	                	System.out.println("shanchu****************");
	                    isOK = false;
	                    localFile.deleteOnExit();
	                    log.info("下载文件" + ftpFileName + "失败");
	                }
	               
	                out.flush();
	            }catch (Exception e)
	            {
	                isOK = false;
	                localFile.deleteOnExit();
	                log.error("FTP服务器下载文件异常：" + e.getMessage());
	            }
	           
	            try
	            {
	                if(null != out)
	                {
	                    out.close();
	                }
	            }
	            catch (IOException e)
	            {
	                log.error(ftpFileName + "下载成功，关闭输出流异常：" + e.getMessage());
	            }
	       
	       System.out.println("jieguo--------"+isOK);
	        return isOK;
	    }
	 public static void closeServer()
	    {
	        log.info("关闭FTP连接");
	       
	        if(ftp.isConnected())
	        {
	            try
	            {
	                ftp.logout();
	                ftp.disconnect();
	            }
	            catch (IOException e)
	            {
	                log.error("FTP断开连接异常：" + e.getMessage());
	            }
	            finally
	            {
	                if(ftp.isConnected())
	                {
	                    try
	                    {
	                        ftp.disconnect();
	                    }
	                    catch (IOException e)
	                    {
	                        log.error("FTP断开连接异常：" + e.getMessage());
	                    }
	                }
	            }
	        }
	       
	    }
	 
}
