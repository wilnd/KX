package mocent.Monitor.Service.impl;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import mocent.Monitor.Dao.CarDao;
import mocent.Monitor.Entity.Car;
import mocent.Monitor.Service.CarService;

@Service
public class CarServiceImpl extends BaseServiceImpl<Car, Integer> implements CarService{
	
	@Resource
	private CarDao carDao;
	
	@Resource
	private void setCarDao(CarDao carDao)
	{
		super.setBaseDao(carDao);
	}
	
	/**
	 * 查询车辆详细信息
	 */
	public Car searchCarInfo(Map<String, String> conMap)
	{
		return carDao.searchCarInfo(conMap);
	}
	
	/**
	 * 离线车辆列表
	 */
	public List<Car> loadOfflineCars()
	{
		return carDao.loadOfflineCars();
	}

	public List findCarByNum(Map<String, String> map) throws Exception {
	
		return carDao.findCarByNum(map);
	}
	
	public Integer findCarByNumCount(Map<String, String> map) throws Exception {
		
		return carDao.findCarByNumCount(map);
	}
}