package mocent.Monitor.Dao;

import java.util.List;
import java.util.Map;

import mocent.Monitor.Entity.Car;

public interface CarDao extends BaseDao<Car, Integer>{
	
	/**
	 * 查询车辆
	 * @param conMap
	 * @return
	 */
	public Car searchCarInfo(Map<String, String> conMap);
	
	/**
	 * 查询不在线的车辆
	 * @return
	 */
	public List<Car> loadOfflineCars();
	
	public List findCarByNum(Map<String,String> map) throws Exception;
	
	public Integer findCarByNumCount(Map<String,String> map) throws Exception;
}
