package mocent.Monitor.Base;

import mocent.Monitor.Packet.CmdPacket;


// 收到服务器发来的应答（通知、报告）
// response / report

/**
 * 此类中不继承 Activity LoginResponse 中不能使用 sharedp...
 * @author Administrator
 *
 */
public abstract class McResponse{
	protected short _cmd;
	protected short _sequence;

	public short get_sequence() {
		return _sequence; 
	}

	public McResponse() {
		// TODO Auto-generated constructor stub
		_sequence = 0;
		_cmd = 0;
	}

	public abstract boolean decode(CmdPacket packet);
}
