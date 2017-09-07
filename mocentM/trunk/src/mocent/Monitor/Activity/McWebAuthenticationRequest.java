package mocent.Monitor.Activity;

import mocent.Monitor.Base.McRequest;
import mocent.Monitor.Packet.CmdPacket;
import mocent.Monitor.Util.ByteUtil;
import mocent.Monitor.Util.NTV;

public class McWebAuthenticationRequest extends McRequest{
	
	private int loginId;
	
	public McWebAuthenticationRequest(int loginId)
	{
		super();
		this.loginId = loginId;
	}
	
	@Override
	public byte[] encode(){
		
        CmdPacket packet = new CmdPacket(CmdPacket.MC_Web_Authentication, ByteUtil.IntToByteArray(loginId));
		_sequence = packet.getCmdSeq();
		
		return packet.getBytes();
	}
}
