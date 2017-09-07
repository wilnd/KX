package mocent.uum.controller;

import java.util.List;
import java.util.Map;

import mocent.uum.controller.vo.TreeNodeVo;
import mocent.uum.entity.Role;
import mocent.uum.service.RoleService;
import mocent.uum.service.bo.Per2RoleBo;
import mocent.uum.service.bo.UserInfoBo;
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
 * @date 2016年10月10日  下午2:15:58
 *
 */
@Controller
@RequestMapping("/role")
public class RoleController {
	
	private static final Logger log = Logger.getLogger(RoleController.class);
	
	@Autowired
	private RoleService roleService; 
	
	
	/**
	 * 通过web名获取角色名和id
	 * @param webName
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/query")
	public List<Role> queryRole(String webName,String roleName){
		List<Role> roleList = roleService.queryRole(webName,roleName);
		return roleList;
	}
	
	@ResponseBody
	@RequestMapping("/queryRole")
	public Map<String, List<Role>> queryRole(String webName,Integer userId){
		Map<String, List<Role>> roleInfo = roleService.queryRole(webName,userId);
		return roleInfo;
	}
	
	@ResponseBody
	@RequestMapping("/chooseRole")
	public List<Role> queryRoleByUserId(Integer userId){
		return roleService.queryRole(userId);
	}
	
	/**
	 * 获取web名
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/queryWebName")
	public List<Role> queryWebName(){
		List<Role> roleList = roleService.queryWebName();
		return roleList;
	}
	/**
	 * 获取web名，初始目录，web+角色
	 */
	@ResponseBody
	@RequestMapping("/queryWebNameToTreeNode")
	public PageUtil<TreeNodeVo> queryRoleToVo(BSPage page){
		PageUtil<TreeNodeVo> treeNodeVo= roleService.queryWebNameToTreeNode(null,page);
		return treeNodeVo;
	}
	
	/**
	 *  权限列表，第二次展示，角色+权限
	 */
	@ResponseBody
	@RequestMapping("/queryRoleMenu")
	public PageUtil<TreeNodeVo> queryRoleToVo(String webName,BSPage page){
		PageUtil<TreeNodeVo> treeNodeVo= roleService.queryWebNameToTreeNode(webName,page);
		return treeNodeVo;
	}
	
	/**
	 * 添加角色信息
	 * @param roleName
	 * @param webName
	 * @param permissionId
	 */
	@ResponseBody
	@RequestMapping("/add")
	public Map<String,Object> addRole(String addRoleName,String webName,@RequestParam(value="permissionId",required=false)List<Integer> permissionId){
		Map<String, Object> roleStatus = ObjectFactory.getMap();
		//判断webName是否是新增
		List<Role> roleList = roleService.queryRole(webName,"");
		if(null !=roleList && roleList.size()==0){
			//新增web
			if(permissionId.size() <= 0){
				roleStatus.put("statusCode", "E");
				roleStatus.put("message", "权限id错误！");
			}else{
				roleService.addRole(addRoleName, webName, permissionId);
				roleStatus.put("statusCode", "S");
			}
		}else{
			List<Role> roleInfo = roleService.queryRole(webName, addRoleName);
			if(roleInfo != null && roleInfo.size() > 0){
				roleStatus.put("statusCode", "E");
				roleStatus.put("message", "该用户名以存在！");
				return roleStatus;
			}
			
			if(permissionId.size() <= 0){
				roleStatus.put("statusCode", "E");
				roleStatus.put("message", "权限id错误！");
			}else{
				roleService.addRole(addRoleName, webName, permissionId);
				roleStatus.put("statusCode", "S");
			}
		}
		
		return roleStatus;
	}
	@ResponseBody
	@RequestMapping("/delete")
	public Map<String, Object> deleteRoleInfo(String roleId){
		Map<String, Object> roleDelStatus = ObjectFactory.getMap();
		if(roleId != null && !"".equals(roleId)){
			roleService.deleteRoleById(Integer.valueOf(roleId));
			roleDelStatus.put("statusCode", "S");
		}else{
			roleDelStatus.put("statusCode", "E");
			roleDelStatus.put("message", "角色id不存在或错误！");
		}
		return roleDelStatus;
	};
	
	@ResponseBody
	@RequestMapping("/update")
	public Map<String, Object> updateRoleInfo(@RequestParam(value="permissionId",required=false)List<Integer> permissionId,@RequestParam("uroleName")String roleName,Per2RoleBo prb){
		Map<String, Object> roleUpdateStatus = ObjectFactory.getMap();
		if(permissionId == null){
			roleUpdateStatus.put("statusCode", "E");
			roleUpdateStatus.put("message", "请至少选择一个权限！");//一般是sql异常
			return roleUpdateStatus;
		}
		prb.setPerIdList(permissionId);
		prb.setRoleName(roleName);
		if(prb != null){
			try{
				roleService.updateRole(prb);
				roleUpdateStatus.put("statusCode", "S");
			}catch(Exception e){
				roleUpdateStatus.put("statusCode", "E");
				roleUpdateStatus.put("message", "内部错误");//一般是sql异常
				log.error(e);
			}
		}
		
		return roleUpdateStatus;
	}
	
	@RequestMapping("/updateUserRoles")
	public Map<String, Object> updateUserRoles(Integer userId,List<Integer> roleId){
		Map<String, Object> reviseUserRoles = ObjectFactory.getMap();
		try{
			roleService.updateRole(userId, roleId);
			reviseUserRoles.put("statusCode", "S");
		}catch(Exception e){
			reviseUserRoles.put("statusCode", "E");
			reviseUserRoles.put("message", "内部错误");//一般是sql异常
			log.error(e);
		}
		return reviseUserRoles;
	}
	
	@ResponseBody
	@RequestMapping("/findRoleById")
	public UserInfoBo findRoleById(String roleId){
		return roleService.findRoleById(roleId);
	}
	
	@ResponseBody
	@RequestMapping("/findRole")
	public PageUtil<UserInfoBo> findRoleByName(String roleName,BSPage page){
		if(null !=roleName){
			roleName=roleName.trim();
		}
		PageUtil<UserInfoBo> findRoleByName = roleService.findRoleByName(roleName,page);
		return findRoleByName;
	}
}
