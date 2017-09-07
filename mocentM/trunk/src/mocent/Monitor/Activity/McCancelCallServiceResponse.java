package mocent.Monitor.Activity;

import mocent.Monitor.Base.McResponse;
import mocent.Monitor.Packet.CmdPacket;
import mocent.Monitor.Util.ByteUtil;
import mocent.Monitor.Util.NTV;

public class McCancelCallServiceResponse extends McResponse{
	
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

		if (_cmd == CmdPacket.MC_Cancel_Ter_Warn_Rsp){
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
