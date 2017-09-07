package mocent.uum.entity;

import java.math.BigInteger;

/**
 * @author jiang
 * @date 2016年9月28日  下午5:07:05
 * @Desc 角色实体类
 */
public class Role {
	
	private int id; //id
	
	private String roleName; //角色名
	
	private String webName; //web名 
	
	private BigInteger createDate; //创建时间
	
	private BigInteger lastUpdateDate; //最后一次修改时间
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
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
				+" roleName："+getRoleName()
				+" webName："+getWebName()
				+" createDate："+getCreateDate()
				+" lastUpdateDate："+getLastUpdateDate();
	}
	
	
}
