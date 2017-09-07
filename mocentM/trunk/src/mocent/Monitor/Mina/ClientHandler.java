package mocent.Monitor.Mina;

import java.nio.ByteBuffer;

import mocent.Monitor.Packet.CmdPacket;

import org.apache.log4j.Logger;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;

public class ClientHandler extends IoHandlerAdapter{
	//日志记录器
	private static final Logger logger = Logger.getLogger(ClientHandler.class);

	private ByteBuffer recv_buffer = null;
	
	private boolean mSigned = false;
	/**
	 * 写处理服务端推送的信息的逻辑
	 */
	@Override
	public void messageReceived(IoSession session, Object message)
			throws Exception {
		recv_buffer.limit(16384);
		if(message instanceof ByteBuffer)  
        {                         
			recv_buffer.put((ByteBuffer)message);  
			int next_read_pos = recv_buffer.position();
			recv_buffer.flip();
			int left_count = recv_buffer.limit();

			while (true) {
				int noise_count = CmdPacket.getNoiseCount(recv_buffer);
				// denoise
				if (noise_count > 0) {
					// move packet
					recv_buffer.position(noise_count);
					recv_buffer.compact();
					recv_buffer.flip();
					next_read_pos -= noise_count;
					left_count -= noise_count;
				}
				// parse packet
				CmdPacket packet = new CmdPacket();//
				int next_packet_pos = packet.parseBuffer(recv_buffer);
				if (next_packet_pos > 0) {
					// notify packet receiveid
					processReceivePacket(packet);
					// move packet
					recv_buffer.limit(left_count);
					recv_buffer.position(next_packet_pos);
					recv_buffer.compact();
					recv_buffer.flip();
					left_count -= next_packet_pos;
					next_read_pos -= next_packet_pos;
					if (left_count <= 0)
						break;
				} else
					break;
			}
			if (next_read_pos < 0)
				next_read_pos = 0;
			recv_buffer.position(next_read_pos); 
        }                                     
		
	}

	@Override
	public void messageSent(IoSession session, Object message) throws Exception
	{
		//发送信息
		super.messageSent(session, message);  
	}
	
	@Override  
    public void exceptionCaught(IoSession session, Throwable cause)  
            throws Exception {  
        System.out.println("客户端异常");  
        super.exceptionCaught(session, cause);  
    }  
	
	@Override  
    public void sessionIdle(IoSession session, IdleStatus status) throws Exception {  
		logger.info("-客户端与服务端连接[空闲] - " + status.toString());  
        System.out.println("-客户端与服务端连接[空闲] - " + status.toString());
        super.sessionIdle(session, status);  
        if(session != null){  
            session.close(true);  
        }  
    }  
	
	@Override
	public void sessionCreated(IoSession session) throws Exception {
		
		super.sessionCreated(session);
	}

	@Override
	public void sessionOpened(IoSession session) throws Exception {
		super.sessionOpened(session);
	}
	
	@Override
	public void sessionClosed(IoSession session) throws Exception {
		// TODO Auto-generated method stub
		super.sessionClosed(session);
	}
	
	private void processReceivePacket(CmdPacket packet)
	{
		short cmd_flag = packet.getCmdFlag();
		//心跳包
		if(cmd_flag == packet.MC_TST)
		{
			
		}
	}
}
