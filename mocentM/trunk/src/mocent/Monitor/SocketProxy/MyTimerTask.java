package mocent.Monitor.SocketProxy;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import mocent.Monitor.Memcached.MemCachedUtils;
import mocent.Monitor.Packet.CmdPacket;

public class MyTimerTask extends TimerTask{

	private Timer timer = null;
	
	private static boolean flag = false;
	
	private static MyTimerTask task = null;
	
	private long sendTime = 0;  //上次发送时间
	
	private long HEART_BEAT_RATE = 30 * 1000;  //发送时间间隔，毫秒
	
	public int HEART_BEAT_TIME = 0;     //心跳次数，如果发送次数达到一定数还没收到服务器下发，则认为服务器出问题了，重新连
	
	private NioClient client = null;
	
	private SocketService socketService = null;
	
	public MyTimerTask(NioClient client, SocketService socketService){
		this.socketService = socketService;
		this.client = client;
	}
	
	//单例模式，保持这个对象 
	public static MyTimerTask getInstance(NioClient client, SocketService socketService){  
        if (task == null || flag ) {  
            //当flag == true时，为了解决，timer.cancel()后，重新创建一个timer  
        	task = new MyTimerTask(client, socketService);    
            if (flag){  
                flag = false;  
            }
        }
        return task;  
    }  
	
	public void start(boolean flg) {  
        
        if (timer == null)
        {  
    		timer = new Timer();
        } 
        else 
        {
            //从此计时器的任务队列中移除所有已取消的任务。  
            timer.purge();  
        }  
      
        timer.scheduleAtFixedRate(this, new Date(), HEART_BEAT_RATE);  
    }  
	
	@Override
	public void run()
	{
		
		if(null == MemCachedUtils.get("signed") || Integer.parseInt((String) MemCachedUtils.get("signed")) == 0){
			HEART_BEAT_TIME = 0;
        	socketService.doReconnect();
		}
		
		if (System.currentTimeMillis() - sendTime >= HEART_BEAT_RATE) 
		{
            boolean isSuccess = sendHeartPacket();
           
            if (!isSuccess || HEART_BEAT_TIME > 100) 
            {
            	HEART_BEAT_TIME = 0;
            	socketService.doReconnect(); //重新连接服务器
            }
            else
            {
            	HEART_BEAT_TIME++;
            }
        }
	}
	//销毁定时器
	public void destroyed(){  
		//终止此计时器，丢弃所有当前已安排的任务。(不但结束当前schedule，连整个Timer的线程(即当前的定时任务)都会结束掉)  
		if(timer != null)
		{
			if(task != null)
			{
				task.cancel();
			}
		}
        flag = true;
	}
	
	//发送心跳数据
    boolean sendHeartPacket(){
    	
    	if(client == null){
    		return false;
    	}
    	if(!client.isConnected()){
    		return false;
    	}
    	
    	client.sendData(CmdPacket.buildTstPacket());
		return true;
    }
}
