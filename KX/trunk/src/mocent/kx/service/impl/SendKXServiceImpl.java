package mocent.kx.service.impl;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import mocent.kx.dao.ICreateKXDao;
import mocent.kx.dao.IHistoryKXDao;
import mocent.kx.entity.CarInfo;
import mocent.kx.entity.MocentImagePath;
import mocent.kx.entity.MocentKX;
import mocent.kx.entity.MocentRecord;
import mocent.kx.service.ISendKXService;
import mocent.kx.util.BSPage;
import mocent.kx.util.PageUtil;
import mocent.kx.util.SendKXUtil;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

@Service
public class SendKXServiceImpl implements ISendKXService {
 private static final Logger log = Logger.getLogger(SendKXServiceImpl.class);
	@Autowired
	private ICreateKXDao createKXDao;
	@Autowired
	private IHistoryKXDao historyKXDao;
	
	@Override
	public PageUtil<CarInfo> findCarByKXId(int kxId,int isOnLine) {
		PageUtil<CarInfo> pageReuslt=new PageUtil<CarInfo>();
		List<CarInfo> list=new ArrayList<CarInfo>();
		List<MocentImagePath> findImagePath = createKXDao.findImagePath(kxId);
		Connection connet = SendKXUtil.connet();
		PreparedStatement prepareStatement=null;
		String sql="SELECT id,sim,plate_number,cur_owner_name from car WHERE ver_62 like CONCAT((SELECT `value` from machine_type where uum_carVerId=?),'%')";
		if(isOnLine >0){
			sql+=" and is_online =1";
		}
		StringBuilder builder=new StringBuilder();
		builder.append(sql);
		try {
			for (int i = 1; i < findImagePath.size(); i++) {
				//sql= sql+" UNION "+sql;
				builder.append(" UNION "+sql);
			}
			 prepareStatement = connet.prepareStatement(builder.toString());
			 for (int i = 0; i < findImagePath.size(); i++) {
				 prepareStatement.setInt(i+1, findImagePath.get(i).getCarVerId());
			}
			 ResultSet rs = prepareStatement.executeQuery();
			 while(rs.next()){
					CarInfo car=new CarInfo();
					car.setId(rs.getInt("id"));
					car.setSim(rs.getString("sim"));
					car.setPlateNumber(rs.getString("plate_number"));
					car.setOwnerName(rs.getString("cur_owner_name"));
					list.add(car);
				}
			/*for (MocentImagePath mocentImagePath : findImagePath) {
					 prepareStatement = connet.prepareStatement(sql);
					
					ResultSet rs = prepareStatement.executeQuery();
					while(rs.next()){
						CarInfo car=new CarInfo();
						car.setId(rs.getInt("id"));
						car.setSim(rs.getString("sim"));
						car.setPlateNumber(rs.getString("plate_number"));
						car.setOwnerName(rs.getString("cur_owner_name"));
						list.add(car);
					}
			}*/
		} catch (SQLException e) {
			log.error(e.toString());
			e.printStackTrace();
		}finally{
			try {
				if (null!= prepareStatement) prepareStatement.close();
				SendKXUtil.closeConn(connet);
			} catch (SQLException e) {
				SendKXUtil.closeConn(connet);
				log.error(e.toString());
				e.printStackTrace();
			}
		}
		pageReuslt.setTotalRows(list.size());
		pageReuslt.setData(list);
		return pageReuslt;
	}

	@Override
	public Map<String, Object> addDownTask(Integer kxId, List<Integer> carId) {
		Map<String, Object> map=new HashMap<String, Object>();
		map.put("stateCode", "S");
		List<CarInfo> carInfo=new ArrayList<CarInfo>();
		Connection connet = SendKXUtil.connet();
		PreparedStatement prepareStatement=null;
		List<MocentImagePath> findImagePath = createKXDao.findImagePath(kxId);
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
						cc.setFile(mocentImagePath.getKxImageUrl());
						carInfo.add(cc);
					}
				}
				
			}
			if (null!= prepareStatement) prepareStatement.close();
			SendKXUtil.closeConn(connet);
			
			//执行新增
			String inserSql="INSERT into file_for_download(valid_after,valid_until,time_last_download,issue_time,complete_time,status,car_id,priority,file,type,size)VALUES(?,?,?,?,?,?,?,?,?,?,?)";
			insertBatch(inserSql, carInfo.size(), carInfo);
		} catch (SQLException e) {
			map.put("stateCode", "E");
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
		historyKXDao.updateKXState(listId, 1);
		
		return map;
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

	@Override
	public List<MocentImagePath> findImageByKx(int kxId) {
		return	createKXDao.findImagePath(kxId);
	}

	@Override
	public PageUtil<MocentKX> findKXBySended(int userId, String permissionList,
			BSPage page) {
		int result=historyKXDao.findKXByUserIdCount(0, 1);//1:提交   暂时表示已发送  
		 PageUtil<MocentKX> pageResult=new PageUtil<MocentKX>(page.getCurPage(), result);
		 if(result > 0){
			 pageResult.setData(historyKXDao.findKXByUserId(0, 1,(page.getCurPage()-1)*page.getPageSize(),page.getPageSize()));
		 }
		return pageResult;
	}

	@Override
	public int updateKXState(List<Integer> listId, Integer state,Integer userId) {
			int updateKXState = historyKXDao.updateKXState(listId, state);
			if(updateKXState > 0){
				List<MocentRecord> listRecord=new ArrayList<MocentRecord>();
				String nowTime=String.valueOf(new Date().getTime()/1000); 
				for (Integer kxId : listId) {
					MocentRecord record=new MocentRecord();
					record.setDesc("重新移到待发送列表");
					record.setKxId(kxId);
					record.setCreateDate(nowTime);
					record.setUserId(userId);
					listRecord.add(record);
				}
				int addRecord = historyKXDao.addRecord(listRecord);
				if(addRecord < 1){
					TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
					return -1;
				}
			}
		return 1;
	}
}
