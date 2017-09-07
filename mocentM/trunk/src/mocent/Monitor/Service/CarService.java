package mocent.Monitor.Service;

import java.util.List;
import java.util.Map;

import mocent.Monitor.Entity.Car;

public interface CarService extends BaseService<Car, Integer>{

	/**
	 * 查询车辆详细信息
	 * @param conMap
	 * @return
	 */
	public Car searchCarInfo(Map<String, String> conMap);
	
	/**
	 * 检索离线车辆列表
	 * @return
	 */
	public List<Car> loadOfflineCars();
	/**
	 * 根据手机号，车牌号，车机sim卡号
	 * @param map
	 * @return
	 * @throws Exception
	 */
	 public List findCarByNum(Map<String,String> map) throws Exception;
	 /**
	 * 根据手机号，车牌号，车机sim卡号
	 * @param map
	 * @return
	 * @throws Exception
	 */
	 public Integer findCarByNumCount(Map<String,String> map) throws Exception;
}
