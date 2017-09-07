package mocent.Monitor.SocketProxy;

import java.io.Serializable;

import mocent.Monitor.Base.McRequest;
import mocent.Monitor.Config.ConfigUtil;

import org.apache.log4j.Logger;

public class SocketHandler{
	
	//日志记录器
	private static final Logger logger = Logger.getLogger(SocketHandler.class);
	
	private SocketService socketService = null;
	
	public SocketHandler(){
		socketService = new SocketService();
		ConfigUtil.initlize();
	}
	
	//发送请求
	public int sendRequest(McRequest request) {
		int ret = 5;

		if (socketService != null) {
			ret = socketService.request(request);
		} 
		
		return ret;
	}
	
	//连接服务器
	public void connectServer()
	{
		if(socketService != null)
		{
			socketService.connect();
		}
	}
	
	//与服务器断开连接
	public void disConnectServer()
	{
		logger.debug("与服务器断开连接：disConnectServer");
		if(socketService != null)
		{
			socketService.disconnect();
		}
	}
}
