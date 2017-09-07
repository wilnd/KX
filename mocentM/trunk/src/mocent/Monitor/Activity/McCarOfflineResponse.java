package mocent.Monitor.Activity;

import mocent.Monitor.Base.McResponse;
import mocent.Monitor.Packet.CmdPacket;
import mocent.Monitor.Util.ByteUtil;

public class McCarOfflineResponse extends McResponse{
	/**
	 * 车辆ID
	 */
	private int car_id;
	
	public int getCar_id()
	{
		return car_id;
	}
	
	@Override
	public boolean decode(CmdPacket cmdPacket)
	{
		boolean ret = false;
		_cmd = cmdPacket.getCmdFlag();
		_sequence = cmdPacket.getCmdSeq();

		if (_cmd == CmdPacket.MC_Car_offline){
			byte[] bytes = cmdPacket.getCmdData();
			int length = bytes.length;
			if(length == 0)
				return false;
			
			
			car_id = ByteUtil.BytesToInt(bytes);
			
			return true;
		}
		return ret;
	}
}
