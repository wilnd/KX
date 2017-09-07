package mocent.kx.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import mocent.kx.entity.MocentImagePath;
import mocent.kx.entity.MocentKX;
import mocent.kx.entity.TreeNode;
import mocent.kx.service.IHistoryKXService;
import mocent.kx.util.BSPage;
import mocent.kx.util.PageUtil;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@RequestMapping("/history")
@Controller
public class HistoryKXController {
	@Autowired
	private IHistoryKXService historyKXService;
	
	@ResponseBody
	@RequestMapping("/getNodes")
	public List<TreeNode> getNodes(Integer id,Integer year,Integer month,Integer day,Integer userId,Integer lv){
		if(null !=lv){
			if(0 == lv){
				year=0;
				month=0;
			}else if(1 == lv){
				month=0;
			}
		}
		return historyKXService.getNodes(id,year, month, day,userId);
	}
	
	@ResponseBody
	@RequestMapping("/findKXById")
	public Map<String,Object> findKXById(int kxId){
		 Map<String,Object> map=new HashMap<String, Object>();
		 map.put("kxObj", historyKXService.findKXById(kxId));
		 MocentImagePath findKXImage = historyKXService.findKXImage(kxId);
		 if(null !=findKXImage){
			 findKXImage.setKxImageUrl(findKXImage.getKxImageUrl());
		 }
		 map.put("image", findKXImage);
		return  map;
	}
	
	@ResponseBody
	@RequestMapping("/findKXByUserId")
	public PageUtil<MocentKX> findKXByUserId(int userId,String permissionList,BSPage page){
		return historyKXService.findKXByUserId(userId, permissionList, page);
	}
	
	
	@ResponseBody
	@RequestMapping("/findWillSend")
	public PageUtil<MocentKX> findWillSend(BSPage page){
		return historyKXService.findWillSend(page);
	}
	@ResponseBody
	@RequestMapping("/updateKXState")
	public Map<String,Object> updateKXState(String listId,Integer state){
		Map<String,Object> map=new HashMap<String, Object>();
		String[] split = listId.split(",");
		List<Integer> list=new ArrayList<Integer>();
		for (String string : split) {
			if(null !=string && !"".equals(string)){
				list.add(Integer.valueOf(string));
			}
		}
		int updateKXState = historyKXService.updateKXState(list, state);
		if(updateKXState>0){
			map.put("stateCode", "S");
		}else{
			map.put("stateCode", "E");
		}
		return map;
	}
	@ResponseBody
	@RequestMapping("/deleteKX")
	public Map<String,Object> deleteKX(String listId){
		String[] split = listId.split(",");
		List<Integer> list=new ArrayList<Integer>();
		for (String string : split) {
			if(null !=string && !"".equals(string)){
				list.add(Integer.valueOf(string));
			}
		}
		return historyKXService.deleteKX(list);
	}
	
}
