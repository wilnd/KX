package mocent.uum.service.impl;

import java.util.List;

import mocent.uum.controller.vo.TreeNodeVo;
import mocent.uum.dao.PermissionMapper;
import mocent.uum.dao.RelevancyMapper;
import mocent.uum.entity.Permission;
import mocent.uum.service.PermissionService;
import mocent.uum.util.BSPage;
import mocent.uum.util.PageUtil;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PermissionServiceImpl implements PermissionService{

	@Autowired
	private PermissionMapper permissisonMapper;
	
	@Autowired
	private RelevancyMapper rema;
	
	@Override
	public List<Permission> queryPer(String perName) {
		return permissisonMapper.selectPermission(perName);
	}

	@Override
	public void addPermissionInfo(Permission per) {
		if(per.getParentId() == -1){
			permissisonMapper.insertPermission(per);
			int insertBeforeId = permissisonMapper.getParentId();
			permissisonMapper.insertPermissionParentId(insertBeforeId);
		}else{
			permissisonMapper.insertPermission(per);
			int insertBeforeId = permissisonMapper.getParentId();
			permissisonMapper.insetPermissionParentIdByConditionId(per.getParentId(),insertBeforeId);
		}
		
	}
	
	@Override
	public PageUtil<TreeNodeVo> queryPermissionMenu(int roleId,BSPage page){
		int result = permissisonMapper.selectRolePermissionNum(roleId);
		
		PageUtil<TreeNodeVo> pageResult = new PageUtil<TreeNodeVo>(page.getCurPage(),result);
		pageResult.setData(permissisonMapper.selectPermissionByRoleId(roleId,(page.getCurPage()-1)*page.getPageSize(),page.getPageSize()));
		
		return pageResult;
	}

	@Override
	public void deletePermission(List<Integer> perId) {
		if(perId.size() > 0){
			rema.deletePermissionByPerId(perId);
			permissisonMapper.deletePermission(perId);
		}
	}

}
