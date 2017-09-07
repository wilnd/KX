package mocent.kx.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.log4j.Logger;

public class TextAndCmdUtil {

	
	private static final Logger log = Logger.getLogger(TextAndCmdUtil.class);
	
	/**
	 * 用于写入text或者bat/sh 命令的
	 * @param content 内容
	 * @param path  路径 不带文件名
	 * @param name  文件名
	 * @return
	 * @throws Exception
	 */
	public static boolean produceFile(String content,String path,String name) throws Exception{
		//判断文件夹
		File f = new File(path);
			if(!f.exists()){
			  f.mkdirs();
		   } 
		//判断文件
		 File file=new File(path+"/"+name);
		  if(!file.exists()){
			  file.createNewFile();
		  }
		  file.setExecutable(true);
		  FileOutputStream o=null;
		  try {
			o = new FileOutputStream(file);  
			//  o.write(content.getBytes("GB2312"));  
			o.write(content.getBytes("GBK"));
		//	System.out.println(content.getBytes("GBK"));
			  o.flush();
			  o.close();
			  return true;
		} catch (Exception e) {
			log.error(e.toString());
			e.printStackTrace();
			return false;
		} 
		
	}
	/**
	 * 执行命令   合成图片
	 * @param path 路径 带有文件名
	 * @return
	 */
	public static boolean synFile(String path){
		System.out.println(path);
        String[] cmd = new String[] {path};  // 命令
        try {
            Process process = Runtime.getRuntime().exec(cmd);
            process.getInputStream();  // 获取执行cmd命令后的信息
            process.getInputStream().close();
            int exitValue = process.waitFor();   
            log.info("返回值：" + exitValue);
            process.getOutputStream().flush();
            process.getOutputStream().close();  // 不要忘记了一定要关
            process.destroy();
            return true;
        } catch (Exception e) {
        	log.error(e.toString());
            e.printStackTrace();
            return false;
        }
	}
}
