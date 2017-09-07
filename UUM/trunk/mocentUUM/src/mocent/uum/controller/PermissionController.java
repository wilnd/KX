package mocent.uum.controller;

import java.util.List;
import java.util.Map;

import mocent.uum.controller.vo.TreeNodeVo;
import mocent.uum.entity.Permission;
import mocent.uum.entity.Role;
import mocent.uum.service.PermissionService;
import mocent.uum.service.RoleService;
import mocent.uum.util.BSPage;
import mocent.uum.util.ObjectFactory;
import mocent.uum.util.PageUtil;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
/**
 * 
 * @author jiang
 * @date 2016年10月10日  下午2:16:03
 * 
 */
@RequestMapping("/permission")
@Controller
public class PermissionController {
	private static final Logger log = Logger.getLogger(PermissionController.class);
	
	@Autowired
	private PermissionService permissionServie;
	
	@Autowired
	private RoleService roleService;
	
	@ResponseBody
	@RequestMapping("/addPermissionInfo")
	public Map<String, Object> addPermissionInfo(Permission per){
		Map<String, Object> perStatus = ObjectFactory.getMap();
		List<Permission> perList = null;
		if(per.getPermissionName() != null ){
			perList = permissionServie.queryPer(per.getPermissionName());
			if(perList != null && perList.size() > 0){
				perStatus.put("statusCode", "E");
				perStatus.put("message", "权限名已存在！");
			}else{				
				permissionServie.addPermissionInfo(per);
				perStatus.put("statusCode", "S");
				perStatus.put("message", "权限名添加成功！");
			}
		}
			return perStatus;
	
	}
	
	@ResponseBody
	@RequestMapping("/queryPermissionInfo")
	public Map<String, Object> queryPermissionInfo(Permission per){
		Map<String, Object> perStatus = ObjectFactory.getMap();
		List<Permission> perList = null;
		if(per.getPermissionName() == null ){
			perList = permissionServie.queryPer("");
			perStatus.put("parentLevel", perList);
			perStatus.put("statusCode", "S");
			return perStatus;
		}else{
			if(per.getPermissionName() != null ){	
				perList = permissionServie.queryPer(per.getPermissionName());
				perStatus.put("parentLevel", perList);
			}
			return perStatus;
		}
	}
	@ResponseBody
	@RequestMapping("/queryPermissionMenu")
	public PageUtil<TreeNodeVo> queryPermission(Integer roleId,BSPage page){
		if(roleId != null){
			PageUtil<TreeNodeVo> treeNodeVo= permissionServie.queryPermissionMenu(roleId,page);
			return treeNodeVo;
		}
		return null;
	}
	
	@ResponseBody
	@RequestMapping("/query")
	public Map<String, Object> queryPermission(){
		Map<String, Object> perInfo= ObjectFactory.getMap();
		List<Permission> queryPer = permissionServie.queryPer("");
		if(null != queryPer && queryPer.size() > 0){
			perInfo.put("permission",queryPer);
			perInfo.put("statusCode", "S");
			return perInfo;
		}else{			
			perInfo.put("statusCode", "E");
		}
		return perInfo;
	}
	
	@ResponseBody
	@RequestMapping("/delete")
	public Map<String, Object> queryPermission(@RequestParam(value="permissionId",required=false)List<Integer> permissionId){
		Map<String, Object> perInfo= ObjectFactory.getMap();
		if(permissionId.size() > 0){
			permissionServie.deletePermission(permissionId);
			perInfo.put("statusCode","S");
			perInfo.put("message", "权限删除成功！");
			return perInfo;
		}
		perInfo.put("statusCode","E");
		perInfo.put("message", "权限删除失败！");
		return perInfo;
	}
}
