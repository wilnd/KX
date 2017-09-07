package mocent.kx.service;

import java.util.List;
import java.util.Map;

import mocent.kx.entity.MocentCar;
import mocent.kx.entity.MocentImagePath;
import mocent.kx.entity.MocentKX;


public interface ICreateKXService {
	/**
	 * 从美图秀秀上下载我截取的图片
	 * @param strUrl 美图秀秀图片路径
	 * @param updateUrl  要下载服务器路径
	 * @param fileName   文件名字
	 */
	public  void writeFile(String strUrl,String updateUrl,String fileName);
	
	public List<MocentCar> findValidCarVer();
	
	public Map<String,Object> addKXInfo(MocentKX mocentKX,String contentStr,String imageUrl);
	
	public MocentKX  findKXById(int kxId);
	
	public List<MocentImagePath> findImagePath(int kxId);
	
	public Map<String,Object> updateKXInfo(MocentKX mocentKX,String contentStr,String imageUrl,List<Integer> newCarList,List<MocentImagePath> oldCarList);
	
}
