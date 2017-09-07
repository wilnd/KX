package mocent.uum.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import mocent.uum.controller.vo.TreeNodeVo;
import mocent.uum.dao.PermissionMapper;
import mocent.uum.dao.RelevancyMapper;
import mocent.uum.dao.RoleMapper;
import mocent.uum.entity.Role;
import mocent.uum.service.RoleService;
import mocent.uum.service.bo.Per2RoleBo;
import mocent.uum.service.bo.UserInfoBo;
import mocent.uum.util.BSPage;
import mocent.uum.util.PageUtil;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RoleServiceImpl implements RoleService {

	private static final Logger log = Logger.getLogger(RoleServiceImpl.class);
	
	@Autowired
	private RoleMapper roleMapper;
	
	@Autowired
	private RelevancyMapper relevancyMapper;
	
	@Autowired
	private PermissionMapper perMapper;

	@Override
	public List<Role> queryRole(String webName,String roleName) {
		return roleMapper.selectRole(webName,roleName);
		
	}
	
	public Map<String,List<Role>> queryRole(String webName,int userId){
		Map<String,List<Role>> roleInfo = new HashMap<String, List<Role>>();
		List<Role> allRole = roleMapper.selectRole(webName,""); //通过web名查出对应的所有角色
		List<Role> userRole = roleMapper.selectRoleByUserId(userId); //通过用户id查询出用户
		for(int i=0; i<userRole.size(); i++){
			for(int k=0; k<allRole.size(); k++){
				if(userRole.get(i).getId() == allRole.get(k).getId()){
					allRole.remove(k);
				}
			}
		}
		
		roleInfo.put("include",userRole);
		roleInfo.put("exclude", allRole);
		
		return roleInfo;
	}
	@Override
	public List<Role> queryRole(int userId){
		return roleMapper.selectRoleByUserId(userId);
	}
	@Override
	public List<Role> queryWebName() { 
		return roleMapper.selectWebName();
	}
	
	/**
	 * 权限页面展示
	 */
	@Override
	public PageUtil<TreeNodeVo> queryWebNameToTreeNode(String webName,BSPage page){
		int result = 5;
		PageUtil<TreeNodeVo> pageResult= null;
		
		if(webName == null || webName == ""){ //第一次初始化
			result = roleMapper.selectWebNum();
			pageResult = new PageUtil<TreeNodeVo>(page.getCurPage(),result);
			if(result >0){
				pageResult.setData(roleMapper.selectWebNameToTreeNode((page.getCurPage()-1)*page.getPageSize(),page.getPageSize()));
			}
			return pageResult;
		}
		
		result = perMapper.selectRoleNum(webName);
		pageResult = new PageUtil<TreeNodeVo>(page.getCurPage(),result);
		pageResult.setData(perMapper.selectPermissionByWebName(webName,(page.getCurPage()-1)*page.getPageSize(),page.getPageSize()));
		
		return pageResult;
	}
	
	@Override
	public void addRole(String roleName, String webName, List<Integer> permissionId) {
		//插入角色表，返回id
		int roleId = roleMapper.insertRole(roleName, webName);
		
		//将权限id插入角色权限关联表
		relevancyMapper.inserPer2Role(roleId,permissionId);
	}
	
	@Override
	public void deleteRoleById(int roleId){	
		
		//删除角色权限关联表信息,并将角色id封装成对象
		List<Per2RoleBo> prb = new ArrayList<Per2RoleBo>();
		Per2RoleBo pr = new Per2RoleBo();
		pr.setRoleId(roleId);
		prb.add(pr);
		relevancyMapper.deletePer2Role(prb);
		
		//删除角色表信息
		roleMapper.deleteRole(roleId);
	}

	@Override
	public void updateRole(Per2RoleBo prb) throws Exception{
		
		//通过角色id获取数据库的角色权限信息
		List<Per2RoleBo> oldList =relevancyMapper.selectPer2Role(prb.getRoleId());
		
		//获取新权限id，前台传入
		List<Integer> newList = prb.getPerIdList();
		Iterator<Integer> newPermission = newList.iterator();
		
		while(newPermission.hasNext()){
			int newPerId = newPermission.next();
			Iterator<Per2RoleBo> oldPermission = oldList.iterator();
			while(oldPermission.hasNext()){
				if(newPerId == oldPermission.next().getPerId()){
					newPermission.remove();
					oldPermission.remove();
					break;
				}
			}
			
		}
		
		//更新角色信息
		roleMapper.updateRole(prb);
		
		//新集合的数据为新增权限，插入到数据库
		if(newList.size() > 0){			
			relevancyMapper.inserPer2Role(prb.getRoleId(), newList);
		}

		//旧集合里的数据表示是需要删除的，是被去掉选择的权限
		if(oldList.size() > 0){	
			relevancyMapper.deletePer2Role(oldList);
		}

	}

	@Override
	public void updateRole(int userId,List<Integer> roleId){
		List<Role> oldRole = queryRole(userId);
		
		List<Integer> tempList = new ArrayList<Integer>();
		for(Role temp : oldRole){
			tempList.add(temp.getId());
		}

		Iterator<Integer> newList = roleId.iterator();
		
		while(newList.hasNext()){
			Integer in = newList.next();
			Iterator<Integer> oldIterator = tempList.iterator();
			while(oldIterator.hasNext()){
				if(in == oldIterator.next()){ //用户新角色与就角色匹配，
					newList.remove();
					oldIterator.remove();
					break;
				}
			}
		}
		
		//旧角色删除
		if(tempList.size() > 0){
			relevancyMapper.deleteUser2Role(userId, tempList);
		}
		
		//新角色添加
		if(roleId.size() > 0){
			relevancyMapper.insertUser2Role(userId, roleId);
		}
	}
	@Override
	public PageUtil<UserInfoBo> findRoleByName(String roleName,BSPage page) {
		int result=roleMapper.findRoleByNameCount(roleName);
		PageUtil<UserInfoBo> pageResult=new PageUtil<UserInfoBo>(page.getCurPage(),result);
		if(result>0){
			pageResult.setData(roleMapper.findRoleByName(roleName,(page.getCurPage()-1)*page.getPageSize(),page.getPageSize()));
		}
		return pageResult;
	}

	@Override
	public UserInfoBo findRoleById(String roleId) {
		UserInfoBo userInfoBo = roleMapper.findRoleById(roleId);
		userInfoBo.setPermission(","+userInfoBo.getPermission()+",");
		return userInfoBo;
	}
}
