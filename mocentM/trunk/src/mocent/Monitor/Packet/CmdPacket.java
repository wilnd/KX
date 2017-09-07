package mocent.Monitor.Packet;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * f1
 * @author Handscm
 *
 */
public class CmdPacket {
	
	public final static short MC_Web_Authentication = 0xe1;  //登录认证
	public final static short MC_Web_Authentication_Rsp = 0xe1; //登录响应
	
	public final static short MC_Call_Customer = 0x30; //呼叫客服
	
	public final static short MC_Query_Pos = 0x42;	//位置查询
	public final static short MC_Query_Pos_Rsp = 0x42;
	
	public final static short MC_Send_Pos = 0x9; //发送导航点
	public final static short MC_Send_Pos_Rsp = 0x9;
	
	public final static short MC_Car_Pre_Warn = 0x13; //车辆预警告
	
	public final static short MC_Car_online = 0xfe; //车辆上线
	public final static short MC_Car_offline = 0xff; //车辆下线
	
	public final static short MC_Cancel_Ter_Warn = 0x01; //客服取消告警
	public final static short MC_Cancel_Ter_Warn_Rsp = 0x01; //

	public final static short MC_TST = 0x0; // 心跳
	
	public final static byte URL_APK = 0;
	public final static byte URL_MPG = 1;

	private static short CMD_SEQUENCE = 0;

	private byte flag1 = 0;
	private byte flag2 = 0;
	private byte flag3 = 0;
	private short cmd_data_len = 0; // len(cmd_data)
	private short cmd_flag = 0; // command
	private short cmd_seq = 0; // sequence
	private byte[] cmd_data = null; // cmd content, except 3flag len cmd seq, it
									// can be null
	private short cmd_sum = 0;

	// used for report & response
	public CmdPacket() {

	}

	// used for request
	public CmdPacket(short cmd) {
		cmd_flag = cmd;
		cmd_data = null;
		cmd_seq = CMD_SEQUENCE++;
		cmd_data_len = 0;
	}

	// used for reply
	public CmdPacket(short cmd, short seq) {
		cmd_flag = cmd;
		cmd_data = null;
		cmd_seq = seq;
		cmd_data_len = 0;
	}

	// used for request
	public CmdPacket(short cmd, byte[] data) {
		cmd_flag = cmd;
		cmd_data = data;
		cmd_seq = CMD_SEQUENCE++;
		cmd_data_len = (short) data.length;

	}

	// used for reply
	public CmdPacket(short cmd, byte[] data, short seq) {
		cmd_flag = cmd;
		cmd_data = data;
		cmd_seq = seq;
		cmd_data_len = (short) data.length;

	}

	public byte[] getCmdData() {
		return cmd_data;
	}

	public short getCmdFlag() {
		return cmd_flag;
	}

	public short getCmdSeq() {
		return cmd_seq;
	}

	public static byte[] buildTstPacket() {
		CmdPacket packet = new CmdPacket();
		packet.cmd_flag = (short) MC_TST;
		return packet.getBytes();
	}

	public static int getNoiseCount(ByteBuffer buffer) {
		// buffer has been flip: position = 0 && limit has been set;
		int count = 0;
		int length = buffer.limit();
		if (length >= 3) {
			for (count = 0; count < length - 3; count++) {
				if (buffer.get(count) == 0x24 && buffer.get(count + 1) == 0x4d && buffer.get(count + 2) == 0x43) {
					break;
				}
			}
		}

		return count;
	}

	public int parseBuffer(ByteBuffer buffer) {
		int end_index = 0;
		int length = buffer.limit();
		if (length >= 10) {
			buffer.position(0);
			byte[] bufer = buffer.array();
			buffer.position(0);
			flag1 = buffer.get();
			flag2 = buffer.get();
			flag3 = buffer.get();

			if (flag1 == 0x24 && flag2 == 0x4d && flag3 == 0x43) {
				cmd_data_len = (short) (buffer.getShort() - 5);
				if(cmd_data_len + 10 > length){
					buffer.position(0);
					return 0;
				}
				cmd_flag = buffer.getShort();
				cmd_seq = buffer.getShort();
				
//				if (cmd_flag == 0x40) {
//					saveLog(bufer, cmd_data_len + 20);
//				}
				
				if (cmd_data_len > 0) {
					cmd_data = new byte[cmd_data_len];

					for (short i = 0; i < cmd_data_len; i++) {
						cmd_data[i] = buffer.get();
					}
				}

				cmd_sum = (short) (buffer.get() & 0xff);
				int pos = buffer.position();

				buffer.position(3);

				short check_sum = 0;

				for (int i = 3; i < cmd_data_len + 9; i++)
					check_sum ^= (short) (buffer.get(i) & 0xff);
				if (check_sum == cmd_sum)
					end_index = pos;

			}
		}

		return end_index;
	}

	public byte[] getBytes() {
		byte[] bytes = null;

		ByteBuffer bb = ByteBuffer.allocate(cmd_data_len + 10);

		bb.put((byte) 0x24);
		bb.put((byte) 0x4D);
		bb.put((byte) 0x43);
		bb.putShort((short) (cmd_data_len + 5));
		bb.putShort(cmd_flag);

		bb.putShort(cmd_seq);
		if (cmd_data != null) {
			bb.put(cmd_data);
		}

		short check_sum = 0;

		for (int i = 3; i < cmd_data_len + 10; i++)
			check_sum ^= (short) (bb.get(i) & 0xff);

		bb.put((byte) (check_sum & 0xff));

		bytes = bb.array();

		return bytes;

	}
}
