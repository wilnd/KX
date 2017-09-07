package mocent.kx.service.impl;

import mocent.kx.dao.ISettingKXDao;
import mocent.kx.entity.MocentCar;
import mocent.kx.entity.MocentLookUp;
import mocent.kx.service.ISettingKXService;
import mocent.kx.util.BSPage;
import mocent.kx.util.PageUtil;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SettingKXServiceImpl implements ISettingKXService {
	@Autowired
	private ISettingKXDao settingKXDao;

	@Override
	public MocentLookUp findLookUp() {
		return settingKXDao.findLookUp();
	}

	@Override
	public int updateLookUp(MocentLookUp lookUp) {
		return settingKXDao.updateLookUp(lookUp);
	}

	@Override
	public PageUtil<MocentCar> findCarVers(BSPage page) {
		int result =settingKXDao.findCarVersCount();
		PageUtil<MocentCar> pageResult=new PageUtil<MocentCar>(page.getCurPage(), result);
		if(result>0){
			pageResult.setData(settingKXDao.findCarVers((page.getCurPage()-1)*page.getPageSize(),page.getPageSize()));
		}
		return pageResult;
	}

	@Override
	public int addCarVer(MocentCar car) {
		return settingKXDao.addCarVer(car);
	}

	@Override
	public MocentCar findCarVerById(MocentCar car) {
		return settingKXDao.findCarVerById(car);
	}

	@Override
	public int updateCarVer(MocentCar car) {
		return settingKXDao.updateCarVer(car);
	}

}
