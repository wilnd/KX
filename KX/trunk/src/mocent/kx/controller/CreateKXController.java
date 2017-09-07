package mocent.kx.controller;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import mocent.kx.entity.MocentCar;
import mocent.kx.entity.MocentImagePath;
import mocent.kx.entity.MocentKX;
import mocent.kx.service.ICreateKXService;
import mocent.kx.util.ConfigUtil;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@RequestMapping("/createKX")
@Controller
public class CreateKXController {
	
	@Autowired
	private ICreateKXService createKXService;
	
	@ResponseBody
	@RequestMapping("/showCarVers")
	public List<MocentCar> showCarVers(){
		return createKXService.findValidCarVer();
	}
	
	@ResponseBody
	@RequestMapping("/uploadImage")
	public String uploadImage(String iamgeUrl,String filename){
		if("".equals(ConfigUtil.cardics_svr)){
			ConfigUtil.initlize();
		}
		Date now=new Date();
		 Calendar cal = Calendar.getInstance(); 
		 cal.setTime(now); 
		  
	    int year = cal.get(Calendar.YEAR); 
	    int month = cal.get(Calendar.MONTH) + 1; 
	    int day = cal.get(Calendar.DAY_OF_MONTH); 
	    if(null == filename || filename.indexOf(".jpg") == -1){
	    	filename=".jpg";
	    }
		String uploadUrl=ConfigUtil.uploadUrl+year+"/"+month+"/"+day;
		String imageName="kx"+now.getTime()+filename;
		createKXService.writeFile(iamgeUrl, uploadUrl, imageName);
		return uploadUrl+"/"+imageName;
	}
	@ResponseBody
	@RequestMapping("/addKXInfo")
	public Map<String,Object> addKXInfo(MocentKX mocentKX,String contentStr,String imageUrl){
	  return createKXService.addKXInfo(mocentKX,contentStr,imageUrl);
	}
	
	@ResponseBody
	@RequestMapping("/updateKXInfo")
	public Map<String,Object> updateKXInfo(MocentKX mocentKX,String contentStr,String imageUrl,String newCarList,String oldCarList){
		String jsonOldCarListStr="{\"oldCarList\":"+oldCarList+"}";
		JSONObject jsonobject = JSONObject.fromObject(jsonOldCarListStr);
		JSONArray array = jsonobject.getJSONArray("oldCarList");
		List<MocentImagePath> list=new ArrayList<MocentImagePath>();
		for (int i = 0; i < array.size(); i++) { 
			JSONObject object = (JSONObject)array.get(i);
			MocentImagePath path=(MocentImagePath)JSONObject.toBean(object,MocentImagePath.class);
			if(null!=path){
				list.add(path);
			}
		}
		String[] split = newCarList.split(",");
		List<Integer> newCarList1=new ArrayList<Integer>();
		for (String id : split) {
			if(null !=id && !"".equals(id)){
				newCarList1.add(Integer.valueOf(id));
			}
		}
		 return createKXService.updateKXInfo(mocentKX, contentStr, imageUrl, newCarList1, list);
	}
	@ResponseBody
	@RequestMapping("/findKXById")
	public Map<String,Object> findKXById(int kxId){
		 Map<String,Object> map=new HashMap<String, Object>();
		 map.put("kxObj", createKXService.findKXById(kxId));
		 map.put("imagePath", createKXService.findImagePath(kxId));
		return map;
	}
}
