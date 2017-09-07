package mocent.Monitor.SocketProxy;

import java.util.Date;

import org.apache.log4j.Logger;

import com.google.common.eventbus.Subscribe;

import mocent.Monitor.Action.Assist;
import mocent.Monitor.Activity.McCallCustomerService;
import mocent.Monitor.Activity.McCancelCallServiceResponse;
import mocent.Monitor.Activity.McCarOfflineResponse;
import mocent.Monitor.Entity.CallService;
import mocent.Monitor.Packet.CmdPacket;

public class EventListener {

	//日志记录器
	private static final Logger logger = Logger.getLogger(EventListener.class);
	 @Subscribe
	public void Listen(CmdPacket packet)
	{
		 short cmd_flag = packet.getCmdFlag();
		 switch(cmd_flag)
		 {
		 	case CmdPacket.MC_Call_Customer:
		 		McCallCustomerService callServer = new McCallCustomerService();
		 		if(callServer.decode(packet))
		 		{
		 			CallService server = callServer.get_server();
		 			server.setCall_time(new Date());
		 			Assist.putServer(server);
		 		}
		 		break;
		 	case CmdPacket.MC_Car_offline:
		 		McCarOfflineResponse onlineRsp = new McCarOfflineResponse();
		 		if(onlineRsp.decode(packet))
		 		{
		 			
		 		}
		 		break;
		 	case CmdPacket.MC_Car_online:
		 		McCarOfflineResponse offlineRsp = new McCarOfflineResponse();
		 		if(offlineRsp.decode(packet))
		 		{
		 			
		 		}
		 		break;
		 	case CmdPacket.MC_Query_Pos_Rsp:
		 		break;
		 	case CmdPacket.MC_Cancel_Ter_Warn_Rsp:
		 		//取消告警
		 		McCancelCallServiceResponse cancelServer = new McCancelCallServiceResponse();
		 		if(cancelServer.decode(packet))
		 		{
		 			Assist.removeServer(String.valueOf(cancelServer.getCar_id()));
		 		}
		 		break;
		 	default:
		 		logger.debug("未识别的操作指令");
		 		break;
		 }
	}
}
