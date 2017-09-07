package mocent.kx.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import mocent.kx.entity.CarInfo;
import mocent.kx.entity.MocentImagePath;
import mocent.kx.entity.MocentKX;
import mocent.kx.service.ISendKXService;
import mocent.kx.util.BSPage;
import mocent.kx.util.ConfigUtil;
import mocent.kx.util.PageUtil;
import mocent.kx.util.UserUtil;
import mocent.kx.util.FtpUtils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@RequestMapping("/sendKX")
@Controller
public class SendKXController {

	@Autowired
	private ISendKXService sendKXService;
	
	@ResponseBody
	@RequestMapping("/updateKXState")
	public Map<String,Object> updateKXState(String listId,Integer state,Integer userId){
		Map<String,Object> map=new HashMap<String, Object>();
		String[] split = listId.split(",");
		List<Integer> list=new ArrayList<Integer>();
		for (String string : split) {
			if(null !=string && !"".equals(string)){
				list.add(Integer.valueOf(string));
			}
		}
		int updateKXState = sendKXService.updateKXState(list, state,userId);
		if(updateKXState>0){
			map.put("stateCode", "S");
		}else{
			map.put("stateCode", "E");
		}
		return map;
	}
	@ResponseBody
	@RequestMapping("/findKXBySended")
	public PageUtil<MocentKX> findKXBySended(int userId,String permissionList,BSPage page){
		return sendKXService.findKXBySended(userId, permissionList, page);
	}
	
	@ResponseBody
	@RequestMapping("/findCarByKXId")
	public PageUtil<CarInfo> findCarByKXId(Integer kxId,Integer isOnLine){
		return sendKXService.findCarByKXId(kxId,isOnLine);
	}
	
	@ResponseBody
	@RequestMapping("/addDownTask")
	public Map<String,Object> addDownTask(Integer kxId,String carId){
		if("".equals(ConfigUtil.cardics_svr)){
			ConfigUtil.initlize();
		}
		Map<String,Object> map=new HashMap<String, Object>();
		//先上传文件
		FtpUtils.connectServer(ConfigUtil.ftphost, ConfigUtil.ftpport, ConfigUtil.ftpuser, ConfigUtil.ftppassword, ConfigUtil.ftpcharset, true);
		List<MocentImagePath> findImageByKx = sendKXService.findImageByKx(kxId);
		for (MocentImagePath mocentImagePath : findImageByKx) {
			String url=mocentImagePath.getKxImageUrl();
			if(!FtpUtils.uploadStep("", url.substring(url.lastIndexOf("/")+1), url.substring(0,url.lastIndexOf("/")), ConfigUtil.ftpcharset)){
				 map.put("stateCode", "E");
				 FtpUtils.closeServer();
				 return map;
			}
		}
		FtpUtils.closeServer();
		
		String message= UserUtil.stateCode(kxId, carId);
		map.put("stateCode", message);
		return map;
	}
	/*String login = UserUtil.isLogin(userId);
	if(null !=login && "false".equals(login)){
		System.out.println("aa");
	}*/
}
