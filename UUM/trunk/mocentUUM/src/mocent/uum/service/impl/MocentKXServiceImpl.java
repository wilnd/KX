package mocent.uum.service.impl;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import mocent.uum.dao.IMocentKXMapper;
import mocent.uum.entity.CarInfo;
import mocent.uum.entity.MocentImagePath;
import mocent.uum.service.IMocentKXService;
import mocent.uum.util.ConfigUtil;
import mocent.uum.util.FtpUtils;
import mocent.uum.util.ImageUtil;
import mocent.uum.util.SendKXUtil;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MocentKXServiceImpl implements IMocentKXService {
	 private static final Logger log = Logger.getLogger(MocentKXServiceImpl.class);
	 @Autowired
	 private IMocentKXMapper mocentKXMapper;
	@Override
	public String addDownTask(Integer kxId, List<Integer> carId) {
		String result="S";
		List<CarInfo> carInfo=new ArrayList<CarInfo>();
		Connection connet = SendKXUtil.connet();
		PreparedStatement prepareStatement=null;
		log.info("down");
		List<MocentImagePath> findImagePath = mocentKXMapper.findImagePath(kxId);
		log.error("====="+findImagePath);
		//上传服务器  从ftp下载
		if(!uploate(findImagePath)){
			result="E";
			SendKXUtil.closeConn(connet);
			return result;
		}
		try {
			String sql="SELECT id from car WHERE ver_62 like CONCAT((SELECT `value` from machine_type where uum_carVerId=?),'%')";
			for (MocentImagePath mocentImagePath : findImagePath) {
				 prepareStatement = connet.prepareStatement(sql);
				prepareStatement.setInt(1, mocentImagePath.getCarVerId());
				ResultSet rs = prepareStatement.executeQuery();
				while(rs.next()){
					int id=rs.getInt("id");
					if(carId.contains(id)){
						CarInfo cc=new CarInfo();
						cc.setId(id);
						cc.setFile(mocentImagePath.getLinuxUrl());
						carInfo.add(cc);
					}
				}
				
			}
			if (null!= prepareStatement) prepareStatement.close();
			SendKXUtil.closeConn(connet);
			
			//执行新增
			if(null !=carInfo && carInfo.size()>0){
				String inserSql="INSERT into file_for_download(valid_after,valid_until,time_last_download,issue_time,complete_time,status,car_id,priority,file,type,size)VALUES(?,?,?,?,?,?,?,?,?,?,?)";
				insertBatch(inserSql, carInfo.size(), carInfo);
			}
		} catch (SQLException e) {
			result="E";
			log.error(e.toString());
			e.printStackTrace();
		}finally{
			try {
				if (null!= prepareStatement) prepareStatement.close();
				SendKXUtil.closeConn(connet);
			} catch (SQLException e) {
				log.error(e.toString());
				e.printStackTrace();
			}
		}
		
		
		List<Integer> listId=new ArrayList<Integer>();
		listId.add(kxId);
		mocentKXMapper.updateKXState(listId, 1);
		
		return result;
	}
	 public void insertBatch(String sql,int total,List<CarInfo> carInfo){
			int m=total%100 == 0?total/100 :(total/100)+1;
			Connection connet=null;
			try {
				for(int i=0;i<m;i++){
					 connet = SendKXUtil.connet();
					connet.setAutoCommit(false);
					  PreparedStatement prepareStatement = connet.prepareStatement(sql); 
					  int n=total-(100*i);
					  for(int j=0;j<n;j++){
						  	int index=100*i+j;
							 long currentTimeMillis = System.currentTimeMillis();
							 prepareStatement.setLong(1,(currentTimeMillis+300000)/1000);
							 prepareStatement.setLong(2, (currentTimeMillis+259200000)/1000);//259200000 等于三天
							 prepareStatement.setLong(3, 0);
							 prepareStatement.setLong(4, currentTimeMillis/1000);
							 prepareStatement.setLong(5, 0);
							 prepareStatement.setInt(6, 1);
							 prepareStatement.setInt(7, carInfo.get(index).getId());
							 prepareStatement.setInt(8, 0);
							 prepareStatement.setString(9, carInfo.get(index).getFile());
							 prepareStatement.setInt(10, 0);
							 prepareStatement.setLong(11, new File(carInfo.get(index).getFile()).length());
							 prepareStatement.addBatch();
					  }
					  prepareStatement.executeBatch();
					  connet.commit();
					  prepareStatement.close(); 
					  SendKXUtil.closeConn(connet);
				}
			} catch (SQLException e) {
					log.error(e.toString());
				e.printStackTrace();
				try {
					if(null !=connet) connet.rollback();
				} catch (SQLException e1) {
					log.error(e.toString());
					e1.printStackTrace();
				}
			}
		}

	 public boolean  uploate(List<MocentImagePath> findImagePath){
		 if("".equals(ConfigUtil.cardics_svr)){
				ConfigUtil.initlize();
			}
		 FtpUtils.connectServer(ConfigUtil.ftphost, ConfigUtil.ftpport, ConfigUtil.ftpuser, ConfigUtil.ftppassword, ConfigUtil.ftpcharset, true);
		 Calendar cal = Calendar.getInstance(); 
		 cal.setTime(new Date()); 
		  
	    int year = cal.get(Calendar.YEAR); 
	    int month = cal.get(Calendar.MONTH) + 1; 
	    int day = cal.get(Calendar.DAY_OF_MONTH);
	    String uploadUrl=ConfigUtil.imageUrl+year+"/"+month+"/"+day;
		for (int i = 0; i < findImagePath.size(); i++) {
			String url=findImagePath.get(i).getKxImageUrl();
			String fileName=url.substring(url.lastIndexOf("/")+1);
			if(FtpUtils.downloadStep("", fileName, uploadUrl, fileName, "GBK")){
				findImagePath.get(i).setLinuxUrl(uploadUrl+"/"+fileName);
			}else{
				FtpUtils.closeServer();
				return false;
			}
		}
		FtpUtils.closeServer();
		return true;
	 }
}
