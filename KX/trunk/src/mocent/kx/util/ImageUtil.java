package mocent.kx.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;



import java.util.List;

import mocent.kx.entity.MocentImagePath;

import org.apache.log4j.Logger;

public class ImageUtil {

	 private static final Logger log = Logger.getLogger(ImageUtil.class);
	 /**
	  * 根据url下载美图的图片
	  * @param url
	  * @param updateUrl
	  * @param fileName
	  */
		public static void writeFile(URL url, String updateUrl,String fileName) {
			log.info("======writeFile"+url.toString()+"=="+updateUrl+"/"+fileName);
			InputStream input = null;
			try {
				input=url.openStream();
				log.info("======init input");
			} catch (IOException e) {
				log.error(e.toString());
				e.printStackTrace();
			}
			OutputStream os = null;
			//判断文件夹
			File f = new File(updateUrl);
			log.info("======shifoucunzai"+f.exists());
				if(!f.exists() && !f.isDirectory()){
				  f.mkdirs();
				  log.info("======chuangjianmulu"+f.exists());
			   } 
				try {
					os = new FileOutputStream(updateUrl+"/"+fileName);
					 log.info("======shuchuliu"+os.toString());
					int bytesRead = 0;
					byte[] buffer = new byte[8192];
					while((bytesRead = input.read(buffer,0,8192))!=-1){
						os.write(buffer,0,bytesRead);
						 log.info("======xierutup********");
					//	os.flush();
					}
				} catch (FileNotFoundException e) {
					log.error(e.toString());
					e.printStackTrace();
				}catch (Exception e) {
					log.error(e.toString());
					e.printStackTrace();
				}finally{
					
					try {
						if(null !=os) os.close();
						if(null !=input) input.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
					
				}		
		}
		
		/**
		 * 复制出多张图片  因为一张快讯可能有多张图片
		 * @param imgAdress
		 * @param updateUrl
		 * @param fileName
		 * @return
		 */
	public static boolean copyFile(String imgAdress,String updateUrl,String fileName){
		File srcFile=new File(imgAdress);
		if(!srcFile.exists()){
			log.error("error:copyFile-原图不存在 ");
			return false;
		}
		 File destFile = new File(updateUrl+"/"+fileName);
		 destFile.setExecutable(true);
		int byteread = 0;//读取的位数
		InputStream in = null;
		OutputStream out = null;
		try {
			 in = new FileInputStream(srcFile);
			 out = new FileOutputStream(destFile);
			 byte[] buffer = new byte[1024];
			  while ((byteread = in.read(buffer)) != -1) {
				  out.write(buffer, 0, byteread);
			  }
			  return true;
		} catch (FileNotFoundException e) {
			log.error(e.toString());
			e.printStackTrace();
			return false;
		}catch (Exception e) {
			log.error(e.toString());
			e.printStackTrace();
			return false;
		}finally{
			try {
				if(null != out) out.close();
				if(null != in) in.close();
			} catch (IOException e) {
				log.error(e.toString());
				e.printStackTrace();
			}
		}
	}
	
	public static boolean deleteFile(String fileUrl){
		File file=new File(fileUrl);
		if(file.exists()){
			file.delete();
		}
		return true;
	}
	
	public static void deleteFileList(List<MocentImagePath> findImagePath){
		for (final MocentImagePath mocentImagePath : findImagePath) {
			new Thread(new Runnable() {
				@Override
				public void run() {
					String url=mocentImagePath.getKxImageUrl();
					int lastIndexOf = url.lastIndexOf("/");
					String autoName=url.substring(lastIndexOf+1, url.lastIndexOf("."));
					ImageUtil.deleteFile(url);
					ImageUtil.deleteFile(url.substring(0,lastIndexOf)+"/"+autoName+"txt");
					ImageUtil.deleteFile(url.substring(0,lastIndexOf)+"/"+autoName+ConfigUtil.cmdSuff);
				}
				
			}).start();
		}
	}
}
