package mocent.uum.service.bo;

import java.util.List;

/**
 * 
 * @author jiang
 * @date 2016年10月10日  上午11:15:19
 * @desc 用于修改角色，关系权限
 */
public class Per2RoleBo {
	
	private int roleId; //角色id
	private int perId; //权限id
	private List<Integer> perIdList;//权限id集合，主要用于前台修改角色时的多个权限，传到后台
	private int per2RoleId; //角色权限关联表id
	private String roleName; //角色名
	private String webName; //web名
	
	public int getRoleId() {
		return roleId;
	}
	public void setRoleId(int roleId) {
		this.roleId = roleId;
	}
	public int getPerId() {
		return perId;
	}
	public void setPerId(int perId) {
		this.perId = perId;
	}
	public List<Integer> getPerIdList() {
		return perIdList;
	}
	public void setPerIdList(List<Integer> perIdList) {
		this.perIdList = perIdList;
	}
	public int getPer2RoleId() {
		return per2RoleId;
	}
	public void setPer2RoleId(int per2RoleId) {
		this.per2RoleId = per2RoleId;
	}
	public String getRoleName() {
		return roleName;
	}
	public void setRoleName(String roleName) {
		this.roleName = roleName;
	}
	public String getWebName() {
		return webName;
	}
	public void setWebName(String webName) {
		this.webName = webName;
	}
	@Override
	public String toString() {
		return "roleId: "+getRoleId()+" perId: "+getPerId()+" per2RoleId: "+getPer2RoleId()+" roleName: "+getRoleName();
	}
	
}
