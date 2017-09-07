package mocent.Monitor.SocketProxy;

import java.io.Serializable;
import java.nio.ByteBuffer;
import java.util.List;
import java.util.Set;

import mocent.Monitor.Activity.McWebAuthenticationRequest;
import mocent.Monitor.Base.McRequest;
import mocent.Monitor.Config.ConfigUtil;
import mocent.Monitor.Memcached.MemCachedUtils;
import mocent.Monitor.Packet.CmdPacket;
import mocent.Monitor.SocketProxy.NioClient.SocketListener;

import org.apache.log4j.Logger;

/**
 * @author win7
 *
 */
public class SocketService implements Serializable{
	
	//日志记录器
	private static final Logger logger = Logger.getLogger(SocketService.class);
		
	private NioClient mConn = null;

	private McRequest[] pending_data = new McRequest[1]; 
	private static ByteBuffer recv_buffer = null;

	static{
		recv_buffer = ByteBuffer.allocate(16384);
		recv_buffer.limit(16384);
	}
	
	private SocketListener mSocketListener = new SocketListener(){
		@Override
		public void onAddressUnresolved(){
			logger.info("onAddressUnresolved");
			MemCachedUtils.set("signed", "0");
			EventManager.getInstance().sendEvent(new BaseEvent(BaseEvent.EVENT_SOCKET, BaseEvent.SE_ADDRESS_UNRESOLVED));
		}
		@Override
		public void onSocketConnected(){
			logger.info("onSocketConnected");
			
			List<Integer> loginId = (List<Integer>) MemCachedUtils.get("loginId");
			
			McWebAuthenticationRequest loginRequest = new McWebAuthenticationRequest(loginId.get(loginId.size()-1));
			mConn.sendData(loginRequest.encode());
			if (pending_data[0] == null) 
			{
				EventManager.getInstance().sendEvent(new BaseEvent(BaseEvent.EVENT_SOCKET, BaseEvent.SE_CONNECTED));
			}
			else
			{
				sendPendingData();
			}
			start();
		}
		@Override
		public void onSocketConnecting(){
			logger.info("onSocketConnecting");
			if (pending_data == null) 
			{
				EventManager.getInstance().sendEvent(new BaseEvent(BaseEvent.EVENT_SOCKET, BaseEvent.SE_CONNECT_PENDING));
			}
		}
		@Override
		public void onSocketConnectTimeout(){
			logger.info("onSocketConnectTimeout");
			MemCachedUtils.set("signed", "0");
			destoryed();
			EventManager.getInstance().sendEvent(new BaseEvent(BaseEvent.EVENT_SOCKET, BaseEvent.SE_CONNECT_TIMEOUT));
		}
		@Override
		public void onSocketDisconnected(){
			logger.info("onSocketDisconnected");
			MemCachedUtils.set("signed", "0");
			destoryed();
			EventManager.getInstance().sendEvent(new BaseEvent(BaseEvent.EVENT_SOCKET, BaseEvent.SE_DISCONNECTED));
		}

		@Override
		public void onSocketError(String error){
			logger.info("onSocketError:" + error);
			MemCachedUtils.set("signed", "0");
			destoryed();
			EventManager.getInstance().sendEvent(new BaseEvent(BaseEvent.EVENT_SOCKET, BaseEvent.SE_ERROR, 0, error));
		}
		@Override
		public void onSocketDataReceived(ByteBuffer buf){
			logger.info("onSocketDataReceived");
			recv_buffer.limit(16384);
			recv_buffer.put(buf);
			int next_read_pos = recv_buffer.position();
			recv_buffer.flip();
			
			int left_count = recv_buffer.limit();
			
			while (true) {
				int noise_count = CmdPacket.getNoiseCount(recv_buffer);
				// denoise
				if (noise_count > 0) {
					// move packet
					recv_buffer.position(noise_count);
					recv_buffer.compact();
					recv_buffer.flip();
					next_read_pos -= noise_count;
					left_count -= noise_count;
				}
				// parse packet
				CmdPacket packet = new CmdPacket();//
				int next_packet_pos = packet.parseBuffer(recv_buffer);
				if (next_packet_pos > 0) {
					// notify packet receiveid
					processReceivePacket(packet);
					// move packet
					recv_buffer.limit(left_count);
					recv_buffer.position(next_packet_pos);
					recv_buffer.compact();
					recv_buffer.flip();
					left_count -= next_packet_pos;
					next_read_pos -= next_packet_pos;
					if (left_count <= 0)
						break;
				} else
					break;
			}
			if (next_read_pos < 0)
				next_read_pos = 0;
			recv_buffer.position(next_read_pos);
		}
		
		@Override
		public void onSocketDataSent(byte[] data)
		{
			logger.info("onSocketDataSent");
			if(data == null || data.length < 8){
				return;
			}
			short cmd = (short) (data[5] << 8 | data[6]);
			
			EventManager.getInstance().sendEvent(new BaseEvent(BaseEvent.EVENT_SOCKET, BaseEvent.SE_CMD_SENT, (int) cmd));
			
		}
	};
	
	public void connect(){
		logger.info("connect");
		if(mConn == null)
		{
			logger.info("连接服务器：" + ConfigUtil.Server_Host_Ip + "成功!");
			mConn = new NioClient(ConfigUtil.Server_Host_Ip, ConfigUtil.Server_Host_Port, mSocketListener);
		}
		
		if (mConn.isConnected())
		{
			if (!ConfigUtil.Server_Host_Ip.equals(mConn.get_host()) || ConfigUtil.Server_Host_Port != mConn.get_port()) 
			{
				doReconnect();
			}
			else if (MemCachedUtils.get("signed").equals("0")) 
			{
				mSocketListener.onSocketConnected();
			}
		} 
		else 
		{
			doConnect();
		}
	}
	
	public void doReconnect()
	{
		logger.info("doReconnect");
		Thread thread = new Thread(new Runnable() {
			@Override
			public void run() 
			{
				try 
				{
					if(mConn != null)
					{
						mConn.disconnect();
					}
					mConn = new NioClient(ConfigUtil.Server_Host_Ip, ConfigUtil.Server_Host_Port, mSocketListener);
					mConn.connect();
				} 
				catch (Exception e) 
				{
					e.printStackTrace();
				}
			}
		});
		thread.start();
	}
	//连接服务器
	private void doConnect() 
	{
		logger.info("doConnect");
		Thread thread = new Thread(new Runnable() {
			@Override
			public void run() {
				try
				{
					mConn.connect();
				}
				catch (Exception e) 
				{
					e.printStackTrace();
				}
			}
		});
		thread.start();
	}
	//断开连接
	public void disconnect()
	{
		logger.info("disconnect");
		if ((mConn != null)) 
		{
			if (mConn.isConnected()) 
			{
				Thread thread = new Thread(new Runnable() {
					@Override
					public void run() {
						try 
						{
							mConn.disconnect();
							destoryed();
						} 
						catch (Exception e) 
						{
							e.printStackTrace();
						}
					}
				});
				thread.start();
			}
		}
		// mPendingPacket = null;
	}
	
	public SocketService()
	{
		logger.info("SocketService");
	}
	
	private void processReceivePacket(CmdPacket packet)
	{
		short cmd_flag = packet.getCmdFlag();
		logger.info("processReceivePacket cmd:" + cmd_flag);
		
		if(cmd_flag == CmdPacket.MC_Web_Authentication_Rsp)
		{
			MemCachedUtils.set("signed", "1");
		}
		
		if(cmd_flag == CmdPacket.MC_TST)
		{
			MyTimerTask.getInstance(mConn, this).HEART_BEAT_TIME = 0;
		}
		else
		{
			EventManager.eventBus.register(new EventListener());
			EventManager.eventBus.post(packet);
		}
	}

	private void saveToPendingData(McRequest request) {
		if(request instanceof McWebAuthenticationRequest)
		{
			pending_data[0] = request;
		}
	}
	
	private void sendPendingData() {
		
		McRequest request = pending_data[0];
		if (request != null) {
			byte[] data = request.encode();
			if (data != null) {
				mConn.sendData(data);
				pending_data[0] = null;
			}
		}
		
	}
	
	public int request(McRequest request)
	{
		
		logger.info("request");
		byte[] data = request.encode();
		if (data == null) 
		{
			return 5;
		}
		int ok = 3;
		if (mConn != null)
		{
			ok = 2;
			if (mConn.isConnected()) 
			{
				ok = 1;
				if (MemCachedUtils.get("signed").equals("1") || request instanceof McWebAuthenticationRequest) 
				{
					mConn.sendData(data);
					ok = 0;
				} 
			} 
			else 
			{
				saveToPendingData(request);
				Thread thread = new Thread(new Runnable() {
					@Override
					public void run() {
						try {
							mConn.connect();
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				});
				thread.start();
				EventManager.getInstance().sendEvent(new BaseEvent(BaseEvent.EVENT_SHOW_TOAST, 0, 0));
			}
		}
		return ok;
	}
	
	//启动定时任务
	private void start(){  
		MyTimerTask.getInstance(mConn, this).start(true);  
    }  
      
	//销毁定时任务
    private void destoryed(){  
    	MyTimerTask.getInstance(mConn, this).destroyed();  
    }  
}
