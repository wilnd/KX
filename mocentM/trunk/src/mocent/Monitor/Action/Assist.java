package mocent.Monitor.Action;

import java.util.Date;
import java.util.HashMap;

import mocent.Monitor.Entity.CallService;
import mocent.Monitor.Memcached.MemCachedUtils;
import mocent.Monitor.SocketProxy.SocketHandler;

public class Assist {
	
	public static int loginId = 1;
	
	public static SocketHandler handler;
	
	public static HashMap<String, CallService> serverList = new HashMap<String, CallService>();;
	
	public static boolean isSigned;
	
	static{
		handler = new SocketHandler();
	}
	
	/**
	 * 将用户呼叫的内容装入哈希表中，待处理
	 * @param server
	 */
	public static void putServer(CallService server)
	{
		if(serverList.containsKey(server.getCar_id()))
		{
			Date time1 = serverList.get(server.getCar_id()).getCall_time();
			Date time2 = server.getCall_time();
			//如果呼叫间隔大于5s,而且之前的呼叫未处理,替换原来的呼叫
			if(time2.getTime() - time1.getTime() > 5000 && serverList.get(server.getCar_id()).getState() != 100)
			{
				serverList.replace(String.valueOf(server.getCar_id()), server);
			}
		}
		else
		{
			//如果没有原来用户没有呼叫，直接插入
			serverList.put(String.valueOf(server.getCar_id()), server);
		}
	}
	
	/**
	 * 移除用户呼叫
	 * @param car_id
	 */
	public static void removeServer(String car_id)
	{
		if(serverList.containsKey(car_id))
		{
			serverList.remove(car_id);
		}
	}
}
