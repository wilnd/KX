package mocent.Monitor.Activity;

import mocent.Monitor.Packet.CmdPacket;
import mocent.Monitor.Util.NTV;
import mocent.Monitor.Base.McRequest;

public class McSendPointRequest extends McRequest{
	
	/*车辆ID*/
	private int car_id; 
	
	/*类型*/
	private int type;
	
	/*是否立即发送*/
	private int isNagivative;
	
	/*经度*/
	private String lat;
	
	/*纬度*/
	private	String lon;
	
	/*地区名称*/
	private String name;
	
	public McSendPointRequest(int carId, int type, int isNagivative, String lat, String lon, String name){
		this.car_id = carId;
		this.type = type;
		this.isNagivative = isNagivative;
		this.lat = lat;
		this.lon = lon;
		this.name = name;
	}
	
	@Override
	public byte[] encode() {
		NTV ntv = new NTV();
		
		ntv.setInt("id", car_id);
		ntv.setInt("p", type);
		ntv.setInt("f", isNagivative);
		ntv.setInt("x", (int)(Double.valueOf(lon) * 1e6));
		ntv.setInt("y", (int)(Double.valueOf(lat) * 1e6));
		ntv.setString("n", name, "GBK");
		CmdPacket packet = new CmdPacket(CmdPacket.MC_Send_Pos, ntv.serialize());

		_sequence = packet.getCmdSeq();

		return packet.getBytes();
	}
}