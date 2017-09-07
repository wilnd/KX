package mocent.kx.dao;

import java.util.List;

import mocent.kx.entity.MocentCar;
import mocent.kx.entity.MocentLookUp;
import mocent.kx.util.BSPage;

public interface ISettingKXDao {

	public MocentLookUp findLookUp();
	
	public int updateLookUp(MocentLookUp lookUp);
	
	public int findCarVersCount();
	
	public List<MocentCar> findCarVers(int startLimt,int pageSize);
	
	public int addCarVer(MocentCar car);
	
	public int updateCarVer(MocentCar car);
	
	public MocentCar findCarVerById(MocentCar car);
}
