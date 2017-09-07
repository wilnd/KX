package mocent.Monitor.Activity;

import mocent.Monitor.Base.McResponse;
import mocent.Monitor.Packet.CmdPacket;
import mocent.Monitor.Util.NTV;

public class McWebAuthenticationResponse extends McResponse{
	
	private int _error_code;
	public int get_error_code(){
		return _error_code;
	}
	
	@Override
	public boolean decode(CmdPacket packet) {
		boolean ret = false;
		_cmd = packet.getCmdFlag();
		_sequence = packet.getCmdSeq();
		if (_cmd == CmdPacket.MC_Web_Authentication_Rsp) {
			NTV ntv = NTV.parse(packet.getCmdData());
			if (ntv != null) {
				ret = true;
				_error_code = ntv.getInt("r");
			}
		}
		return ret;
	}
}
