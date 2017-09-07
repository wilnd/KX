package mocent.Monitor.Activity;

import mocent.Monitor.Base.McRequest;
import mocent.Monitor.Packet.CmdPacket;
import mocent.Monitor.Util.ByteUtil;

public class McCancelCallServiceRequest extends McRequest{
	private int car_id;
	
	public McCancelCallServiceRequest(int car_id)
	{
		super();
		this.car_id = car_id;
	}
	
	@Override
	public byte[] encode(){
		
        CmdPacket packet = new CmdPacket(CmdPacket.MC_Cancel_Ter_Warn, ByteUtil.IntToByteArray(car_id));
		_sequence = packet.getCmdSeq();
		
		return packet.getBytes();
	}
}
