package mocent.kx.dao;

import java.util.List;

import mocent.kx.entity.MocentCar;
import mocent.kx.entity.MocentImagePath;
import mocent.kx.entity.MocentKX;

public interface ICreateKXDao {

	public List<MocentCar> findValidCarVer();
	
	public int addMocentKX(MocentKX mocentKX);
	
	public int findMocentKXCount(long zero,long twelve);
	
	public int addImagePath(List<MocentImagePath> imagePath);
	
	public MocentKX findKXById(int kxId);
	
	public List<MocentImagePath> findImagePath(int kxId);
	
	public int updateMocentKX(MocentKX mocentKX);
	
	public int delMocentPathByKXId(int kxId);
	
	public int delMocentPathById(List<MocentImagePath> imagePath);
}
