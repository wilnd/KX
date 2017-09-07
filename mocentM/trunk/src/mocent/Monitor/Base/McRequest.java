package mocent.Monitor.Base;

// 向服务器发送请求（回复）
//
// request / reply

public abstract class McRequest {
	protected short _sequence;

	public short get_sequence() {
		return _sequence;
	}

	public McRequest() {
		// TODO Auto-generated constructor stub
		_sequence = 0;
	}

	public abstract byte[] encode();
	


}
