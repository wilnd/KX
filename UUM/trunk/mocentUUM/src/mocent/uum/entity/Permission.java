package mocent.uum.entity;

import java.math.BigInteger;

/**
 * 
 * @author jiang
 * @date 2016年9月28日  下午5:49:21
 * @Desc 权限实体类
 */
public class Permission {
	
	private int id; //id
	
	private String permissionName; //权限名
	
	private String permissionDesc; //描述
	
	private int parentId;          //父权限id
	
	private BigInteger createDate; //创建时间
	
	private BigInteger lastUpdateDate; //最后一次更新时间

	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getPermissionName() {
		return permissionName;
	}
	public void setPermissionName(String permissionName) {
		this.permissionName = permissionName;
	}
	public String getPermissionDesc() {
		return permissionDesc;
	}
	public void setPermissionDesc(String permissionDesc) {
		this.permissionDesc = permissionDesc;
	}
	public int getParentId() {
		return parentId;
	}
	public void setParentId(int parentId) {
		this.parentId = parentId;
	}
	public BigInteger getCreateDate() {
		return createDate;
	}
	public void setCreateDate(BigInteger createDate) {
		this.createDate = createDate;
	}
	public BigInteger getLastUpdateDate() {
		return lastUpdateDate;
	}
	public void setLastUpdateDate(BigInteger lastUpdateDate) {
		this.lastUpdateDate = lastUpdateDate;
	}
	
	@Override
	public String toString() {
		return "id："+getId()
				+" permissionName："+getPermissionName()
				+" permissionDesc："+getPermissionDesc()
				+" parentId："+getParentId()
				+" createDate："+getCreateDate()
				+" lastUpdateDate："+getLastUpdateDate();
	}
	
}
