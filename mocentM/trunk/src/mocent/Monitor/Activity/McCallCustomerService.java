package mocent.Monitor.Activity;

import mocent.Monitor.Base.McResponse;
import mocent.Monitor.Entity.CallService;
import mocent.Monitor.Packet.CmdPacket;
import mocent.Monitor.Util.ByteUtil;
import mocent.Monitor.Util.NTV;

public class McCallCustomerService extends McResponse{

	public CallService server = new CallService();
	
	public CallService get_server() {
		return server;
	}
	
	@Override
	public boolean decode(CmdPacket packet){
		boolean ret = false;
		_cmd = packet.getCmdFlag();
		_sequence = packet.getCmdSeq();

		if (_cmd == CmdPacket.MC_Call_Customer){
			byte[] bytes = packet.getCmdData();
			int length = bytes.length;
			if(length == 0)
				return false;
			
			int car_id = ByteUtil.ByteArrayToInt(bytes, 0);
			server.setCar_id(car_id);
			server.setLongitude(ByteUtil.ByteArrayToInt(bytes, 10));
			server.setLatitude(ByteUtil.ByteArrayToInt(bytes, 14));
			server.setSpeed(ByteUtil.Byte2ArrayToInt(bytes, 18));
			server.setDirection(ByteUtil.Byte2ArrayToInt(bytes, 20));
			server.setFrom(0);
			return true;
		}
		
		return ret;
	}
}
