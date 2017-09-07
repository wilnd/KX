package mocent.Monitor.SocketProxy;

public class BaseEvent {
	public static final int EVENT_ALL = -1; //所有事件都处理
	public static final int EVENT_SHOW_TOAST = 0; // 用于 执行于非 UI 线程的函数
	public static final int EVENT_SERVICE_CONNECT = 1;
	public static final int EVENT_CONNECTIVITY_CHANGE = 2;
	public static final int EVENT_NETWORK_NOT_READY = 3;
	public static final int EVENT_SOCKET = 4;
	public static final int EVENT_RESPONSE_LOGIN = 5;
	public static final int EVENT_SEND_POS = 6;
	public static final int EVENT_SEND_POS_ANSWER = 7;
	public static final int EVENT_CALL_CUSTOMER = 8;
	public static final int EVENT_CAR_ONLINE = 9;
	public static final int EVENT_CAR_OFFLINE = 10;
	public static final int EVENT_CUSTOMER_CANCEL_WARN = 11;
	public static final int EVENT_CUSTOMER_CANCEL_WARN_ANSWER = 12;
	public static final int EVENT_MC_TST = 13;
	
	
	public static final int SE_ADDRESS_UNRESOLVED = 0;
	public static final int SE_ERROR = 1;
	public static final int SE_CONNECT_TIMEOUT = 2;
	public static final int SE_DISCONNECTED = 3;
	public static final int SE_CONNECT_PENDING = 4;
	public static final int SE_CONNECTED = 5;
	public static final int SE_CMD_RECEIVED = 6;
	public static final int SE_CMD_SENT = 7;
	
	private int _event_id;
	private int _event_param1;
	private int _event_param2;
	private int _id;
	private String _event_param3;
	
	private int _r;
	private int _stat;
	private int _t;
	
	public int get_t() {
		return _t;
	}
	
	public int get_r() {
		return _r;
	}
	public int get_stat() {
		return _stat;
	}
	
	public int get_id() {
		return _id;
	}
	public int get_event_param1() {
		return _event_param1;
	}

	public int get_event_param2() {
		return _event_param2;
	}

	public String get_event_param3() {
		return _event_param3;
	}

	public int get_event_id() {
		return _event_id;
	}

	public BaseEvent(int event_id) {
		this(event_id, 0);
	}

	public BaseEvent(int event_id, int param1) {
		this(event_id,param1,0);
	}

	public BaseEvent(int event_id, int param1, int param2) {
		this(event_id,param1,param2,null);
	}
	
	public BaseEvent(int event_id, int param1, int param2, String param3) {
		_event_id = event_id;
		_event_param1 = param1;
		_event_param2 = param2;
		_event_param3 = param3;
		_id = 0;
		_r = 0;
		_stat = 0;
		_t = 0;
	}
}
