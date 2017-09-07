package mocent.uum.service;

import java.util.List;
import java.util.Map;

import mocent.uum.controller.vo.TreeNodeVo;
import mocent.uum.entity.Role;
import mocent.uum.service.bo.UserInfoBo;
import mocent.uum.util.BSPage;
import mocent.uum.util.PageUtil;
import mocent.uum.service.bo.Per2RoleBo;

public interface RoleService {
	
	/**
	 * 拿到所有角色信息
	 * @return
	 */
	List<Role> queryRole(String webName,String roleName);
	
	Map<String,List<Role>> queryRole(String webName,int userId);
	
	/**
	 * 拿到所有web名
	 * @return
	 */
	List<Role> queryWebName();
	
	List<Role> queryRole(int userId);
	
	PageUtil<TreeNodeVo> queryWebNameToTreeNode(String webName,BSPage page);
	
	/**
	 * 添加角色信息,permissionId,可能是多个
	 */
	void addRole(String roleName,String webName,List<Integer> permissionId);
	
	/**
	 * 通过角色id删除角色信息，并删除关联表信息
	 * @param roleId
	 */
	void deleteRoleById(int roleId);
	
	void updateRole(Per2RoleBo prb) throws Exception;
	
	void updateRole(int userId,List<Integer> roleId);
	
	PageUtil<UserInfoBo> findRoleByName(String roleName,BSPage page);
	
	UserInfoBo findRoleById(String roleId);
}
