package mocent.kx.service.impl;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import mocent.kx.dao.ICreateKXDao;
import mocent.kx.entity.MocentCar;
import mocent.kx.entity.MocentImagePath;
import mocent.kx.entity.MocentKX;
import mocent.kx.service.ICreateKXService;
import mocent.kx.util.ConfigUtil;
import mocent.kx.util.ImageUtil;
import mocent.kx.util.TextAndCmdUtil;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

@Service
public class CreateKXServiceImpl implements ICreateKXService {
	
	@Autowired
	private ICreateKXDao createKXDao;

	 private static final Logger log = Logger.getLogger(CreateKXServiceImpl.class);
	@Override
	public void writeFile(String strUrl, String updateUrl,String fileName){
		try {
			ImageUtil.writeFile(new URL(strUrl), updateUrl, fileName);
		} catch (MalformedURLException e) {
			log.error(e.toString());
			e.printStackTrace();
		}		
	}
	@Override
	public List<MocentCar> findValidCarVer() {
		return createKXDao.findValidCarVer();
	}
	@Override
	public Map<String, Object> addKXInfo(MocentKX mocentKX, String contentStr,String imageUrl) {
		Map<String, Object> map=new HashMap<String, Object>();
		map.put("stateCode", "S");
		if("".equals(ConfigUtil.cardics_svr)){
			ConfigUtil.initlize();
		}
		Date now=new Date();
		 Calendar cal = Calendar.getInstance(); 
		 cal.setTime(now); 
		  
	    int year = cal.get(Calendar.YEAR); 
	    int month = cal.get(Calendar.MONTH) + 1; 
	    int day = cal.get(Calendar.DAY_OF_MONTH);
	    
	    
	    //mocentKX.setKxState(0);//0保存，1提交、2未提交、3采纳、4未采纳5、待发送
	    mocentKX.setKxYear(year);
	    mocentKX.setKxMonth(month);
	    mocentKX.setKxDate(day);
	    mocentKX.setCreateDate(String.valueOf(now.getTime()/1000));
	    String uploadUrl=ConfigUtil.uploadUrl+year+"/"+month+"/"+day;
	    
	    long current=System.currentTimeMillis();
	    long zero=(current/(1000*3600*24)*(1000*3600*24)-TimeZone.getDefault().getRawOffset())/1000;
	    long twelve=zero+24*60*60*1000-1;
	    int findMocentKXCount = createKXDao.findMocentKXCount(zero,twelve)+1;
	    
	    String monthStr=String.valueOf(month);
	    String dayStr=String.valueOf(day);
	    if(month < 10){
	    	monthStr="0"+monthStr;
	    }
	    if(day < 10){
	    	dayStr="0"+dayStr;
	    }
	    String kxNum="KX"+year+monthStr+dayStr;
	    
	    
	    if(findMocentKXCount <10){
	    	kxNum+="A00"+findMocentKXCount;
	    }else if(findMocentKXCount <100 && findMocentKXCount > 9){
	    	kxNum+="A0"+findMocentKXCount;
	    }else{
	    	kxNum+="A"+findMocentKXCount;
	    }
	    mocentKX.setKxNum(kxNum);
	    
	    try {
	    	 createKXDao.addMocentKX(mocentKX);
	    	List<MocentImagePath> imagePathList = getImagePathList(contentStr, mocentKX,uploadUrl,imageUrl);
	    	createKXDao.addImagePath(imagePathList);
	    	if(null !=imageUrl && !"".equals(imageUrl)){
	    		log.info("====delete"+imageUrl);
	    		ImageUtil.deleteFile(imageUrl);
	    	}
	    	map.put("kxNum", mocentKX.getKxNum());
		} catch (Exception e) {
			map.put("stateCode", "E");
			log.error(e.toString());
			e.printStackTrace();
			TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
		}
		return map;
	}
	
	public List<MocentImagePath> getImagePathList(String contentStr,MocentKX mocentKX,String pathUrl,String srcImage) throws Exception{
		SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
		List<MocentImagePath> list=new ArrayList<MocentImagePath>();
		String[] carContent = contentStr.split("▽");
		for (String car : carContent) {
			MocentImagePath imagePathVO=new MocentImagePath();
			String[] car_p = car.split("▼");
			//车典id  id={},
			int carId=Integer.valueOf(car_p[0].substring(3, car_p[0].indexOf(",")));
			String name="kx"+String.valueOf(new Date().getTime());
			imagePathVO.setCarVerId(carId);
			imagePathVO.setKxId(mocentKX.getId());
			imagePathVO.setKxImageUrl(pathUrl+"/"+name+".jpg");
			imagePathVO.setKxTextUrl(pathUrl+"/"+name+".txt");
			
			StringBuilder build=new StringBuilder();
			build.append("m Iptc.Envelope.ServiceId \"Mocent\""+ConfigUtil.newLine);
			build.append("m Iptc.Envelope.ProductId \"Cardics 5 Plus\""+ConfigUtil.newLine);
			build.append("m Iptc.Envelope.UNO \""+mocentKX.getKxNum()+"\""+ConfigUtil.newLine);
			build.append("m Iptc.Application2.Caption \""+mocentKX.getKxTitle()+"\""+ConfigUtil.newLine);
			build.append("m Iptc.Application2.Subject \" \""+ConfigUtil.newLine);
			build.append("m Iptc.Application2.Category \" \""+ConfigUtil.newLine);
			build.append("m Iptc.Application2.Writer \""+mocentKX.getKxCreateByUserName()+"\""+ConfigUtil.newLine);
			build.append("m Iptc.Application2.ReleaseDate \""+sdf.format(new Date())+"\""+ConfigUtil.newLine);
			build.append("m Iptc.Application2.SpecialInstructions \"\""+ConfigUtil.newLine);
			
			for (int i = 1; i < car_p.length; i++) {
				build.append("m Iptc.0x0009.0x001"+i+" \""+car_p[i]+"\""+ConfigUtil.newLine);
			}
			//复制图片
			Boolean flag=ImageUtil.copyFile(srcImage, pathUrl, name+".jpg");
			log.info("复制图片："+flag);
			if(!flag){ throw  new RuntimeException();}
			//生成txt文件
			flag=TextAndCmdUtil.produceFile(build.toString(), pathUrl, name+".txt");
			log.info("生成txt:"+flag);
			if(!flag){ throw  new RuntimeException();}
			//生成命令
			String dos="cd "+ConfigUtil.iptcPath+ConfigUtil.newLine;
			if(ConfigUtil.cmdSuff.equals(".bat")){
				dos+=ConfigUtil.iptcPath.substring(0,2)+ConfigUtil.newLine;
			}
			dos+=imagePathVO.getKxImageUrl()+"<"+imagePathVO.getKxTextUrl()+ConfigUtil.newLine;
			dos+="iptctest.exe "+imagePathVO.getKxImageUrl()+"<"+imagePathVO.getKxTextUrl();
			//System.out.println(dos);
			flag=TextAndCmdUtil.produceFile(dos,pathUrl,name+ConfigUtil.cmdSuff);
			log.info("生成dos："+flag);
			if(!flag){ throw  new RuntimeException();}
			//合成图片
			
			flag=TextAndCmdUtil.synFile(pathUrl+"/"+name+ConfigUtil.cmdSuff);
			log.info("合成:"+flag);
			if(!flag){ throw  new RuntimeException();}
			
			list.add(imagePathVO);
		}
		return list;
	}
	@Override
	public MocentKX findKXById(int kxId) {
		return createKXDao.findKXById(kxId);
	}
	@Override
	public List<MocentImagePath> findImagePath(int kxId) {
		return createKXDao.findImagePath(kxId);
	}
	@Override
	public Map<String, Object> updateKXInfo(MocentKX mocentKX,
			String contentStr, String imageUrl,
			List<Integer> newCarList, List<MocentImagePath> oldCarList) {
		Map<String, Object> map=new HashMap<String, Object>();
		
		map.put("stateCode", "S");
		if("".equals(ConfigUtil.cardics_svr)){
			ConfigUtil.initlize();
		}
		Date now=new Date();
		 Calendar cal = Calendar.getInstance(); 
		 cal.setTime(now); 
		  
	    int year = cal.get(Calendar.YEAR); 
	    int month = cal.get(Calendar.MONTH) + 1; 
	    int day = cal.get(Calendar.DAY_OF_MONTH);
	    
	  //  mocentKX.setKxState(0);//0保存，1提交、2未提交、3采纳、4未采纳5、待发送
	    mocentKX.setKxYear(year);
	    mocentKX.setKxMonth(month);
	    mocentKX.setKxDate(day);
	    mocentKX.setCreateDate(String.valueOf(now.getTime()/1000));
	    String uploadUrl=ConfigUtil.uploadUrl+year+"/"+month+"/"+day;
	    
	    File file=new File(uploadUrl);
	    if(!file.exists()){
	    	file.mkdirs();
	    }
	    
		List<Integer> oldCarid=getOldCarid(oldCarList);
		List<MocentImagePath> delPathId=getDelCarid(newCarList, oldCarList);
		List<Integer> addCarId=getAddCarId(newCarList, oldCarid);
		
		String srcImage="";
		//是否更改图片  只更改了图片
		if(null !=imageUrl && !"".equals(imageUrl) && null!=mocentKX && null !=mocentKX.getKxTitle()  ){
			srcImage=imageUrl;
		}else{
			try {
				srcImage=oldCarList.get(0).getKxImageUrl();
			} catch (Exception e) {
				map.put("StateCode", "E");
				map.put("message", "原图不存在");
				e.printStackTrace();
				return map;
			}
		}
		
		try {
			// 是否更改了内容   修改了图片一定会传内容
			if(null!=mocentKX && null !=mocentKX.getKxTitle() && !"".equals(mocentKX.getKxTitle())){
				//更新快讯表   新增图片  更新图片表
				createKXDao.updateMocentKX(mocentKX);
				List<MocentImagePath> imagePathList = getImagePathList(contentStr, mocentKX, uploadUrl, srcImage);
				//删除快讯的图片
				List<MocentImagePath> findImagePath = createKXDao.findImagePath(mocentKX.getId());
				ImageUtil.deleteFileList(findImagePath);
				createKXDao.delMocentPathByKXId(mocentKX.getId());
				createKXDao.addImagePath(imagePathList);
				
				if(null !=imageUrl && !"".equals(imageUrl)){
		    		ImageUtil.deleteFile(imageUrl);
		    	}
			}else{
				if(null != addCarId && 0 !=addCarId.size() ){
					//新增图片 新增图片表   kx内容查询出来
					//id=1,fdafdsaf▼fdfdsfffdd▼fdfssdffsd▼afdsagdsfa▼gfds▼▼▽id=2,fdafdsaf▼fdfdsfffddfdf▼ssdffsdafdsag▼dsfagfds▼▼▼▽
					String addStr="";
					String[] allContent = contentStr.split("▽");
					for(int i=0;i<addCarId.size();i++){
						for (String string : allContent) {
							if(null !=string && string.indexOf("id="+addCarId.get(i)+",") > -1){
								addStr+=string+"▽";
							}
						}
					}
					List<MocentImagePath> imagePathList = getImagePathList(addStr, mocentKX, uploadUrl, srcImage);
					createKXDao.addImagePath(imagePathList);
				}
				
				if(null != delPathId && 0 !=delPathId.size() ){
					//删除图片表记录
					createKXDao.delMocentPathById(delPathId);
				}
			}
		} catch (Exception e) {
			map.put("stateCode", "E");
			log.error(e.toString());
			e.printStackTrace();
			TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
		}
		map.put("kxNum", mocentKX.getKxNum());
		return map;
	}
	public List<Integer> getOldCarid(List<MocentImagePath> oldCarList){
		List<Integer> oldCarid=new ArrayList<Integer>();
		
		for (MocentImagePath path : oldCarList) {
			oldCarid.add(path.getCarVerId());
		}
		return oldCarid;
	}

	public List<MocentImagePath> getDelCarid(List<Integer> newCarList,List<MocentImagePath> oldCarList){
		List<MocentImagePath> delPathId=new ArrayList<MocentImagePath>();
		for (MocentImagePath path : oldCarList) {
			if(!newCarList.contains(path.getCarVerId())){
				delPathId.add(path);
			}
		}
		return delPathId;
	}

	public List<Integer> getAddCarId(List<Integer> newCarList,List<Integer> oldCarid){
		List<Integer> addCarId=new ArrayList<Integer>();
		for (Integer integer : newCarList) {
			if(!oldCarid.contains(integer)){
				addCarId.add(integer);
			}
		}
		return addCarId;
	}
 
	public List<Integer> getUpdatePath(List<Integer> newCarList,List<Integer> oldCarid){
		List<Integer> updatePathId=new ArrayList<Integer>();
		for (Integer integer : oldCarid) {
			if(newCarList.contains(integer)){
				updatePathId.add(integer);
			}
		}
		return updatePathId;
	}
}
