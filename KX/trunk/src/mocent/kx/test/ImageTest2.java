package mocent.kx.test;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;

import javax.imageio.ImageIO;
import javax.imageio.ImageReadParam;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;

import org.apache.log4j.Logger;

public class ImageTest2 {
	 private static final Logger log = Logger.getLogger(ImageTest2.class);
	
	
	 public void cutImage(File srcImg, OutputStream output, java.awt.Rectangle rect){
        if(srcImg.exists()){
            java.io.FileInputStream fis = null;
            ImageInputStream iis = null;
            try {
                fis = new FileInputStream(srcImg);
                // ImageIO 支持的图片类型 : [BMP, bmp, jpg, JPG, wbmp, jpeg, png, PNG, JPEG, WBMP, GIF, gif]
                String types = "jpg";
                String suffix = null;
                // 获取图片后缀
                if(srcImg.getName().indexOf(".") > -1) {
                    suffix = srcImg.getName().substring(srcImg.getName().lastIndexOf(".") + 1);
                }// 类型和图片后缀全部小写，然后判断后缀是否合法
                if(suffix == null || types.indexOf(suffix.toLowerCase()) < 0){
                    log.error("Sorry, the image suffix is illegal. the standard image suffix is {}." + types);
                    return ;
                }
                // 将FileInputStream 转换为ImageInputStream
                iis = ImageIO.createImageInputStream(fis);
                // 根据图片类型获取该种类型的ImageReader
                ImageReader reader = ImageIO.getImageReadersBySuffix(suffix).next();
                reader.setInput(iis,true);
                ImageReadParam param = reader.getDefaultReadParam();
                param.setSourceRegion(rect);
                BufferedImage bi = reader.read(0, param);
                ImageIO.write(bi, suffix, output);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    if(fis != null) fis.close();
                    if(iis != null) iis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }else {
            log.warn("the src image is not exist.");
        }
    }
	public void cutImage(File srcImg, String destImgPath,String imageName, java.awt.Rectangle rect){
        File destImg = new File(destImgPath);
        if(!destImg.exists()){
        	destImg.mkdirs();
        }
        String p = destImg.getPath();
        try {
            if(!destImg.isDirectory()) p = destImg.getParent();
            if(!p.endsWith(File.separator)) p = p + File.separator;
            cutImage(srcImg, new java.io.FileOutputStream(p+imageName), rect);
        } catch (FileNotFoundException e) {
            log.warn("the dest image is not exist.");
        }
    }
 
	
	 public void cutImage(String srcImg, String destImg,String imageName, int x, int y, int width, int height){
	        cutImage(new File(srcImg), destImg,imageName, new java.awt.Rectangle(x, y, width, height));
	    }
}
