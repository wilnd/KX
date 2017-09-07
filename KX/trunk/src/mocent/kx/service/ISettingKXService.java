package mocent.kx.service;

import mocent.kx.entity.MocentCar;
import mocent.kx.entity.MocentLookUp;
import mocent.kx.util.BSPage;
import mocent.kx.util.PageUtil;

public interface ISettingKXService {
	public MocentLookUp findLookUp();
	
	public int updateLookUp(MocentLookUp lookUp);
	
	public PageUtil<MocentCar> findCarVers(BSPage page);
	
	public int addCarVer(MocentCar car);
	
	public int updateCarVer(MocentCar car);
	
	public MocentCar findCarVerById(MocentCar car);
}
