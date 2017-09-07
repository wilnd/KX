package mocent.Monitor.Action;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;

import mocent.Monitor.Activity.McSendPointRequest;
import mocent.Monitor.Base.Constants;
import mocent.Monitor.Config.ConfigUtil;
import mocent.Monitor.Entity.Car;
import mocent.Monitor.Entity.Owner;
import mocent.Monitor.Http.HttpConnect;
import mocent.Monitor.Http.HttpResponse;
import mocent.Monitor.Memcached.MemCachedUtils;
import mocent.Monitor.Service.CarService;
import mocent.Monitor.Service.OwnerService;
import mocent.Monitor.Util.StringUtil;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.log4j.Logger;

public class CarAction extends BaseAction{
	//日志记录器
    private static final Logger logger = Logger.getLogger(CarAction.class);
	@Resource
	private CarService carService;

	@Resource
	private OwnerService ownerService;
	
	private Car car = new Car();
	
	private Owner owner = new Owner();
	//http请求发送与接收
	private HttpConnect httpConnect = new HttpConnect();
	
	/**
	 * 逆地址解析
	 * @throws Exception
	 */
	public void RegeoAddress() throws Exception 
	{
		String  result = "";
		String lonAndLat = getParameter("lonAndLat");
		try
		{
			String url = String.format(Constants.RegeoUrl, lonAndLat);
			HttpResponse response = httpConnect.sendGet(url, null);
			String ret = new String(response.content, "UTF-8");
			JSONObject obj = JSONObject.fromObject(ret);
			if(obj != null)
			{
				if(obj.get("infocode").equals("10000"))
				{
					JSONObject childObj = JSONObject.fromObject(obj.get("regeocode"));
					String formattedAddress = childObj.getString("formatted_address");
					JSONObject streetObj = JSONObject.fromObject(childObj.get("addressComponent"));
					result = formattedAddress.substring((streetObj.getString("province") + streetObj.getString("city") + streetObj.getString("district")).length(), formattedAddress.length());
				}
			}
			ajaxText(result);
		}
		catch(Exception ex)
		{
			logger.debug("逆地址解析失败", ex);
		}
	}
	
	/**
	 * 加载不在线车辆列表
	 * @throws Exception
	 */
	public void LoadOfflineCars() throws Exception
	{
		List<Car> carList = new ArrayList<Car>();
		try
		{
			carList = carService.loadOfflineCars();
			if(carList != null && carList.size() > 0)
			{
				String retStr = JSONArray.fromObject(carList).toString();
				ajaxJson(retStr);
			}
			else
			{
				ajaxJson("null");
			}
		}
		catch(Exception ex)
		{
			logger.debug("加载不在线车辆列表失败!", ex);
		}
	}
	
	/**
	 * 检索车辆信息
	 * @throws Exception
	 */
	public void SearchCarInfo() throws Exception 
	{
		String plateNumber = getParameter("plateNumber");
		String sim = getParameter("sim");
		String idNumber = getParameter("id");
		String phoneNumber = getParameter("phoneNumber");
		try
		{
			if(StringUtil.isNullOrEmpty(plateNumber) && StringUtil.isNullOrEmpty(phoneNumber)
					&& StringUtil.isNullOrEmpty(sim) && StringUtil.isNullOrEmpty(idNumber))
			{
				ajaxJson("null");
			}
			else
			{
				Map<String, String> conMap = new HashMap<String, String>();
				if(!StringUtil.isNullOrEmpty(plateNumber))
				{
					conMap.put("plateNumber", plateNumber);
				}
				if(!StringUtil.isNullOrEmpty(sim))
				{
					conMap.put("sim", sim);
				}
				if(!StringUtil.isNullOrEmpty(phoneNumber))
				{
					owner = ownerService.getOwnerByPhone(phoneNumber);
					if(owner != null)
					{
						conMap.put("cur_owner_id", String.valueOf(owner.getId()));
					}
				}
				if(!StringUtil.isNullOrEmpty(idNumber))
				{
					conMap.put("id", idNumber);
				}
				car = carService.searchCarInfo(conMap);
				if(car == null)
				{
					ajaxJson("null");
				}
				else
				{
					ajaxJson(JSONArray.fromObject(car).toString());
				}
			}
		}
		catch(Exception ex)
		{
			logger.debug("检索车辆信息失败!", ex);
		}
	}
	
	/**
	 * 发送导航点
	 * @throws Exception
	 */
	public void SendPoint() throws Exception
	{
		String lon = getParameter("lon");
		String lat = getParameter("lat");
		String name = getParameter("name");
		String carId = getParameter("carId");
		String nav = getParameter("nav");
		try
		{
			int ret = -1;
			McSendPointRequest sendPointRequest = new McSendPointRequest(Integer.valueOf(carId), 1, Integer.valueOf(nav), lat, lon, name);
			if(MemCachedUtils.get("signed").equals("1"))
			{
				ret = Assist.handler.sendRequest(sendPointRequest);
			}
			
			ajaxText(String.valueOf(ret));
		}
		catch(Exception ex)
		{
			logger.debug("发送导航点失败!", ex);
		}
	}
	
	/**
	 * 获取车辆详细信息
	 * @throws Exception
	 */
	public void GetDetailInfo() throws Exception
	{
		String carId = getParameter("carId");
		try
		{
			if(!StringUtil.isNullOrEmpty(carId))
			{
				car = carService.get(Integer.valueOf(carId));
				if(car != null)
				{
					String retStr = JSONArray.fromObject(car).toString();
					ajaxJson(retStr);
				}
				else
				{
					ajaxJson("null");
				}
			}
			else
			{
				ajaxJson("null");
			}
		}
		catch(Exception ex)
		{
			logger.debug("加载车辆详细信息失败!" + ex.getMessage());
		}
	}
	
	/**
	 * 页面关闭时 断开socket连接 定时器停止工作
	 * @throws Exception
	 */
	public void OnWindowsClosed() throws Exception
	{
		try
		{
			Assist.handler.disConnectServer();
			ajaxText("true");
		}
		catch(Exception ex)
		{
			logger.debug("断开socket连接失败!", ex);
		}
	}
	/**
	 * 
	 * @param userId
	 * 通过id判断用户是否是特殊权限的用户
	 * userId应该是获取的，不是以形参的方式
	 */
	public void judgeLoginUser(){
		String userId = getParameter("uId");
		if(userId != null){			
			if(ConfigUtil.managerId == Integer.parseInt(userId)){
				ajaxJson(userId);
			}
		}
    }
	
	/**
	 * 检查socket的连接状态
	 * @throws Exception
	 */
	public void CheckSocketStatus() throws Exception
	{
		try
		{
			if(MemCachedUtils.isExist("signed"))
			{
				/*没有登录，重新连接服务器*/
				if("0".equals(MemCachedUtils.get("signed")))
				{
					Assist.handler.connectServer();
				}
				ajaxText("1");
			}
		}
		catch(Exception ex)
		{
			logger.debug("刷新页面，重新连接服务器失败!");
		}
	}
	
	public void getCarInfo()throws Exception{
		Map<String,String> map=new HashMap<String, String>();
		map.put("simNum", getParameter("simNum"));
		map.put("carNum", getParameter("carNum"));
		map.put("phoneNum", getParameter("phoneNum"));
		
		map.put("pageIndex", getParameter("pageIndex"));
		map.put("pageNum", getParameter("pageNum"));
		List findCarByNum = carService.findCarByNum(map);
		if(null !=findCarByNum){
			String retStr = JSONArray.fromObject(findCarByNum).toString();
			ajaxJson(retStr);
		}
	}
	public void getCarInfoCount() throws Exception{
		Map<String,String> map=new HashMap<String, String>();
		map.put("simNum", getParameter("simNum"));
		map.put("carNum", getParameter("carNum"));
		map.put("phoneNum", getParameter("phoneNum"));
		Integer findCarByNumCount = carService.findCarByNumCount(map);
		if(null !=findCarByNumCount){
			String retStr = JSONArray.fromObject(findCarByNumCount).toString();
			ajaxJson(retStr);
		}
	}
}
