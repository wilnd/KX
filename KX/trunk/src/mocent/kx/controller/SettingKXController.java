package mocent.kx.controller;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import mocent.kx.entity.MocentCar;
import mocent.kx.entity.MocentLookUp;
import mocent.kx.service.ICreateKXService;
import mocent.kx.service.ISettingKXService;
import mocent.kx.util.BSPage;
import mocent.kx.util.PageUtil;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@RequestMapping("/setting")
@Controller
public class SettingKXController {

	@Autowired
	private ICreateKXService createKXService;
	@Autowired
	private ISettingKXService settingKXService;
	
	@ResponseBody
	@RequestMapping("/findLookUp")
	public MocentLookUp findLookUp(){
		return settingKXService.findLookUp();
	}
	@ResponseBody
	@RequestMapping("/updateLookUp")
	public Map<String,Object> updateLookUp(MocentLookUp lookUp){
		Map<String,Object> map=new HashMap<String, Object>();
		int result=settingKXService.updateLookUp(lookUp);
		if(result>0){
			map.put("state", "S");
		}else{
			map.put("state", "E");
		}
		return map;
	}
	
	@ResponseBody
	@RequestMapping("/findCarVers")
	public PageUtil<MocentCar> findCarVers(BSPage page){
		return settingKXService.findCarVers(page);
	}
	
	@ResponseBody
	@RequestMapping("/addCarVer")
	public Map<String,Object> addCarVer(MocentCar carVer){
		Map<String,Object> map=new HashMap<String, Object>();
		carVer.setCarCreateDate(String.valueOf(new Date().getTime()/1000));
		int addCarVer = settingKXService.addCarVer(carVer);
		if(addCarVer>0){
			map.put("state", "S");
		}else{
			map.put("state", "E");
		}
		return map;
	}
	
	@ResponseBody
	@RequestMapping("/updateCarVer")
	public Map<String,Object> updateCarVer(MocentCar carVer){
		Map<String,Object> map=new HashMap<String, Object>();
		carVer.setCarCreateDate(String.valueOf(new Date().getTime()/1000));
		int updateCarVer = settingKXService.updateCarVer(carVer);
		if(updateCarVer>0){
			map.put("state", "S");
		}else{
			map.put("state", "E");
		}
		return map;
	}
	
	@ResponseBody
	@RequestMapping("/findCarVerById")
	public Map<String,Object> findCarVerById(MocentCar car){
		Map<String,Object> map=new HashMap<String, Object>();
		car=settingKXService.findCarVerById(car);
		map.put("carVer", car);
		return map;
	}
}
