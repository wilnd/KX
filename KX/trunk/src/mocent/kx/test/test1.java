package mocent.kx.test;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import mocent.kx.entity.CarInfo;
import mocent.kx.entity.MocentImagePath;
import mocent.kx.util.SendKXUtil;

public class test1 {
	public static void main(String[] args) {
	/*	String[] split = "fdasfd▼fdaskfjdska".split("▼");
		
		String[] split1 = "fda▽sfd▼fdaskfj▽dska".split("▼");
		//System.out.println(split1[0].split("▽")[0]);
		
		//System.out.println("d,sa,fds,a".indexOf(","));
		System.out.println("id=123,".substring(3, "id=123,".indexOf(",")));
		
		System.out.println("D:iptc".substring(0,2));*/
	/*String url="E:/image/2016/11/18/1479435465499.jpg";
		int lastIndexOf = url.lastIndexOf("/");
		String autoName=url.substring(lastIndexOf+1, url.lastIndexOf("."));
		System.out.println(autoName);
		System.out.println(url.substring(0,lastIndexOf));*/
	/*	String line="\r\n";
		
		String dos="cd D:iptc"+line;
		dos+="D:"+line;
		dos+="E:/image/2016/11/16/1479269064878.jpg<E:/image/2016/11/16/1479269064878.txt"+line;
		dos+="iptctest.exe E:/image/2016/11/16/1479269064878.jpg<E:/image/2016/11/16/1479269064878.txt";
		
		try {
			System.out.println("生成dos："+TextAndCmdUtil.produceFile(dos,"E:\\image\\2016\\11\\16","1479269064878.bat"));
		} catch (Exception e) {
			
			e.printStackTrace();
		}
		String a="E:\\image\\2016\\11\\16\\1479269064878.bat";
		TextAndCmdUtil.synFile(a);*/
		
	/*	List<CarInfo> list=new ArrayList<CarInfo>();
		Connection connet = SendKXUtil.connet();
		
		PreparedStatement prepareStatement=null;
		String sql="SELECT id,sim,plate_number,cur_owner_name from car WHERE ver_62 like CONCAT((SELECT `value` from machine_type where uum_carVerId=?),'%')";
		for (int i=1;i<3;i++) {
			try {
				 prepareStatement = connet.prepareStatement(sql);
				prepareStatement.setInt(1, 1);
				ResultSet rs = prepareStatement.executeQuery();
				while(rs.next()){
					CarInfo car=new CarInfo();
					car.setId(rs.getInt("id"));
					car.setSim(rs.getString("sim"));
					car.setPlateNumber(rs.getString("plate_number"));
					car.setOwnerName(rs.getString("cur_owner_name"));
					list.add(car);
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		SendKXUtil.closeConn(connet);*/
		
		
		/*SimpleDateFormat sfd=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		try {
			Date parse = sfd.parse("2016-11-23 16:40:00");
			System.out.println(parse.getTime()/1000);
			
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
		try {
			for (int i=1;i<3;i++) {
				Connection connet = SendKXUtil.connet();
				String sql="INSERT INTO test_user_info (user_name,password) VALUES('a','b')";
				connet.setAutoCommit(false);
				PreparedStatement prepareStatement = connet.prepareStatement(sql);
				prepareStatement.execute();
				connet.commit();
				SendKXUtil.closeConn(connet);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
}
