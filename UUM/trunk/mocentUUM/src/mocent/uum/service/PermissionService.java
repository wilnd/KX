package mocent.uum.service;

import java.util.List;

import mocent.uum.controller.vo.TreeNodeVo;
import mocent.uum.entity.Permission;
import mocent.uum.util.BSPage;
import mocent.uum.util.PageUtil;

/**
 * 
 * @author jiang
 * @date 2016年10月10日  下午2:06:07
 *
 */
public interface PermissionService {

	/**
	 * 所有权限
	 * @return
	 */
	List<Permission> queryPer(String PerName); 
	
	PageUtil<TreeNodeVo> queryPermissionMenu(int roleId,BSPage page);
	
	void addPermissionInfo(Permission per);
	
	void deletePermission(List<Integer> perId);
}
