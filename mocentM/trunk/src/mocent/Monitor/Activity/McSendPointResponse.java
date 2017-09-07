package mocent.Monitor.Activity;

import mocent.Monitor.Base.McResponse;
import mocent.Monitor.Packet.CmdPacket;
import mocent.Monitor.Util.NTV;

public class McSendPointResponse extends McResponse
{
	private int error_code;

	public int getError_code() {
		return error_code;
	}
	
	@Override
	public boolean decode(CmdPacket cmdPacket)
	{
		boolean ret = false;
		_cmd = cmdPacket.getCmdFlag();
		_sequence = cmdPacket.getCmdSeq();

		if (_cmd == CmdPacket.MC_Send_Pos_Rsp){
			NTV ntv = NTV.parse(cmdPacket.getCmdData());
			if(ntv == null)
				return false;
			
			error_code = ntv.getInt("r");
			return true;
		}
		
		return ret;
	}
}