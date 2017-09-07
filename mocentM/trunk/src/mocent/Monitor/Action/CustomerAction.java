package mocent.Monitor.Action;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import mocent.Monitor.Activity.McCancelCallServiceRequest;
import mocent.Monitor.Activity.McWebAuthenticationRequest;
import mocent.Monitor.Entity.CallService;
import mocent.Monitor.Entity.Customer;
import mocent.Monitor.Memcached.MemCachedUtils;
import mocent.Monitor.Service.CustomerService;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.log4j.Logger;

public class CustomerAction extends BaseAction{

	//日志记录器
    private static final Logger logger = Logger.getLogger(CustomerAction.class);
    
    @Resource
	private CustomerService customerService;
    
    
    private Customer customer;
    
    public void Login() throws Exception
    {
    	String loginName = getParameter("name");
    	String loginPwd = getParameter("pwd");
    	JSONObject obj = new JSONObject();
    	try
    	{
    		int ret = -1;
    		customer = customerService.get("login_name", loginName);
    		if(customer != null)
    		{
    			if(loginPwd.equals(customer.getLogin_pwd()))
    			{
    				obj.accumulate("errorCode", "5");
    				obj.accumulate("errorMsg", "登录成功!");

    				List<Integer> userSet = (List<Integer>)MemCachedUtils.get("loginId");
    				if(userSet == null || userSet.size() == 0){
    					userSet = new ArrayList<Integer>(); 
    				}
    				
    				if(userSet.contains(customer.getId())){
    					userSet.remove(userSet.indexOf(customer.getId()));
    					userSet.add(customer.getId());
    				}else{
    					userSet.add(customer.getId());
    				}
    				
    				MemCachedUtils.set("loginId",userSet, new Date(1000*60*24));
    				Assist.handler.connectServer();//登录成功，连接服务器
    			}
    			else
    			{
    				obj.accumulate("errorCode", "3");
    				obj.accumulate("errorMsg", "密码错误，请重新输入!");
    			}
    		}
    		else 
    		{
				obj.accumulate("errorCode", "4");
				obj.accumulate("errorMsg", "用户不存在,请重新输入用户名!");
			}
    	}
    	catch(Exception ex)
    	{
    		obj.accumulate("errorCode", "2");
			obj.accumulate("errorMsg", "内部程序错误!");
    		logger.debug(" 登录客服监控系失败!" + ex.getMessage());
    	}
      	String retStr = JSONArray.fromObject(obj).toString();
    	ajaxJson(retStr);
    }
    
    /**
     * 生成登录序列
     * @throws Exception
     */
    public void GenerateLoginSeq() throws Exception
    {
    	String loginId = getParameter("id");
    	try
    	{
    		McWebAuthenticationRequest loginRequest = new McWebAuthenticationRequest(Integer.valueOf(loginId));
    		byte[] seq = loginRequest.encode();
    		ajaxText(new String(seq, "GBK"));
    	}
    	catch(Exception ex)
    	{
    		logger.debug("生成登录序列失败!");
    	}
    }
    /**
     * 获取客服人员信息
     * @throws Exception
     */
    public void GetCustomerInfo() throws Exception
    {
    	String loginName = getParameter("name");
    	try
    	{
    		customer = customerService.get("login_name", loginName);
    		if(customer != null)
    		{
    			String retStr = JSONArray.fromObject(customer).toString();
    			ajaxJson(retStr);
    		}
    		else
    		{
    			ajaxJson("null");
    		}
    	}
    	catch(Exception ex)
    	{
    		logger.debug("加载客服人员信息失败!" + ex.getMessage());
    	}
    }
    
    public void getCustomerInfoById() throws Exception
    {
    	int userId = Integer.valueOf(getParameter("userId"));
    	try
    	{
    		customer = customerService.get(userId);
    		if(customer != null)
    		{
    			String retStr = JSONArray.fromObject(customer).toString();
    			ajaxJson(retStr);
    		}
    		else
    		{
    			ajaxJson("null");
    		}
    	}
    	catch(Exception ex)
    	{
    		logger.debug("加载客服人员信息失败!" + ex.getMessage());
    	}
    }
    
    /**
     * 加载客服呼叫的队列
     * @throws Exception
     */
	@SuppressWarnings("rawtypes")
	public void LoadCallService() throws Exception
    {
    	HashMap<String, CallService> serverList = Assist.serverList;
    	int userId = Integer.valueOf(getParameter("userId"));
    	try
    	{
    		if(serverList.size() > 0)
    		{
    			List<CallService> list = new ArrayList<CallService>();
    			Iterator iter = serverList.entrySet().iterator();
    			while(iter.hasNext())
    			{
    				Map.Entry entry = (Map.Entry) iter.next();
    				CallService val = (CallService)entry.getValue();
    				if(0 !=val.getDeal_id() &&  userId!=val.getDeal_id()){
    					val.setState(3);
    				}
    				if(0 !=val.getDeal_id() &&  userId==val.getDeal_id()){
    					val.setState(1);
    				}
    				list.add(val);
    			}
    			Collections.sort(list,new Comparator<CallService>(){
    				@Override
    				public int compare(CallService o1, CallService o2) {
    					if(o1.getState() > o2.getState()){
    						return 2;
    					}
    					if(o1.getState() == o2.getState()){
    						String i=Long.toString(o1.getCall_time().getTime()-o2.getCall_time().getTime());
    						return Integer.valueOf(i);
    					}
    	                return -1; 
    				}
    			});
    			String retStr = JSONArray.fromObject(list).toString();
    			ajaxJson(retStr);
    		}
    		else
    		{
    			ajaxJson("null");
    		}
    	}
    	catch(Exception ex)
    	{
    		logger.debug("加载服务队列失败!" + ex.getMessage());
    	}
    }
    
    /**
	 * 取消告警
	 * @throws Exception
	 */
	public void CancelTerWarn() throws Exception
	{
		String car_id = getParameter("carId");
		try
		{
			int ret = -1;
			McCancelCallServiceRequest request = new McCancelCallServiceRequest(Integer.valueOf(car_id));
			
			if(MemCachedUtils.get("signed").equals("1"))
			{
				//发送取消告警的请求
				ret = Assist.handler.sendRequest(request);
				if(ret == 0)
				{
					Assist.removeServer(car_id);
				}
			}
			ajaxText(String.valueOf(ret));
		}
		catch(Exception ex)
		{
			logger.debug("客服取消告警失败!" + ex.getMessage());
		}
	}
	public void lockCarUser() throws Exception{
		String car_id = getParameter("carId");
		int userId=Integer.valueOf(getParameter("loginId"));
		int ret = -1;
		HashMap<String, CallService> serverList = Assist.serverList;
		if(null !=serverList  && serverList.size()>0){
			synchronized (serverList) {
			CallService callService = serverList.get(car_id);
			if(null !=callService){
					//从待处理状态变成我在处理
					if(2==callService.getState()){
						callService.setState(1);
						callService.setDeal_id(userId);
						serverList.put(car_id, callService);
						ret=2;
					}else{
						ret=5;
					}
				}else{
					ret=4;
				}
			}
		}
		//-1是没有服务队列 2：是锁定成功4：该服务已不存在5：改服务已被别人锁定
		ajaxText(String.valueOf(ret));
		
	}
	
	/**
	 * 修改用户密码
	 */
	public void modifyUserPwd(){
		
		int id = Integer.valueOf(getParameter("id")).intValue();
		String login_pwd = getParameter("newPwd");
		
		customer = customerService.get(id);
		
		customer.setLogin_pwd(login_pwd);
		customerService.update(customer);
		
		ajaxText("success");
	}
}
