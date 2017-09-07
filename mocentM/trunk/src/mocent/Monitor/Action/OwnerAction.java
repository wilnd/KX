package mocent.Monitor.Action;

import javax.annotation.Resource;

import mocent.Monitor.Entity.Car;
import mocent.Monitor.Entity.Owner;
import mocent.Monitor.Service.CarService;
import mocent.Monitor.Service.OwnerService;
import mocent.Monitor.Util.StringUtil;
import net.sf.json.JSONArray;

import org.apache.log4j.Logger;

public class OwnerAction extends BaseAction{
	//日志记录器
    private static final Logger logger = Logger.getLogger(CarAction.class);

	@Resource
	private OwnerService ownerService;
	
	private Owner owner = new Owner();
	
	/**
	 * 获取车主详细信息
	 * @throws Exception
	 */
	public void GetOwnerInfo() throws Exception
	{
		String id = getParameter("id");
		try
		{
			if(!StringUtil.isNullOrEmpty(id))
			{
				owner = ownerService.get(Integer.valueOf(id));
				if(owner != null)
				{
					ajaxJson(JSONArray.fromObject(owner).toString());
				}
				else
				{
					ajaxJson("null");
				}
			}
		}
		catch(Exception ex)
		{
			logger.debug("获取车主信息失败!", ex);
		}
	}
}
