package mocent.Monitor.Mina;

import mocent.Monitor.Activity.McSendPointRequest;

public class mainTest {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		SocketClient client = new SocketClient();
		client.start();
		
		
		McSendPointRequest request = new McSendPointRequest(351, 0, 0, "23.193105", "113.402466", "宗远丰田");
	}
}
