package mocent.uum.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.log4j.Logger;

public class ImageUtil {
	private static final Logger log = Logger.getLogger(ImageUtil.class);
	public static boolean uploadImage(String srcImage,String upUrl,String fileName){
		FileOutputStream os=null;
		FileInputStream input=null;
		File file=new File(srcImage);
		if(!file.exists()){
			log.error("yuantu"+srcImage+"==="+upUrl+"fileName"+fileName);
			log.error("原图片不存在");
			return false;
		}
		File up=new File(upUrl);
		if(!up.exists()){
			up.mkdirs();
			log.error("chuangjianwenjian"+srcImage+"==="+upUrl+"fileName"+fileName);
		}
		try {
			input=new FileInputStream(file);
			os = new FileOutputStream(upUrl+"/"+fileName);
			int bytesRead = 0;
			byte[] buffer = new byte[8192];
			while((bytesRead = input.read(buffer,0,8192))!=-1){
				os.write(buffer,0,bytesRead);
				log.error("xie**********");
			}
		} catch (FileNotFoundException e) {
			log.error(e.toString());
			e.printStackTrace();
			return false;
		} catch (Exception e) {
			log.error(e.toString());
			e.printStackTrace();
			return false;
		}finally{
		try {
			if(null !=os)  os.close();
			if(null !=input)  input.close();
		} catch (IOException e) {
			log.error(e.toString());
			e.printStackTrace();
		}
	}
		return true;
	}
	
}
